/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.sparkplug

import java.math.BigInteger
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.lifecycle.Mqtt3ClientReconnector
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish

import org.eclipse.tahu.message.SparkplugBPayloadEncoder
import org.eclipse.tahu.message.model.MetricDataType
import org.eclipse.tahu.message.model.SparkplugBPayload
import org.eclipse.tahu.message.SparkplugBPayloadDecoder
import org.eclipse.tahu.model.MetricDataTypeMap

import io.greenglass.node.sparkplug.datatypes.MetricDefinition

import io.greenglass.node.sparkplug.datatypes.MetricNameValue
import io.greenglass.node.sparkplug.datatypes.MetricValue
import io.greenglass.node.sparkplug.datatypes.metricValue
import io.klogging.NoCoLogging
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
enum class NodeState(name: String, val value: Int) {
    Offline("Offline", 0),
    ConnectingToBroker("Connecting to broker", 1),
    WaitingForHostState("Waiting for host", 2),
    Online("Online", 3)
}

@Serializable
data class HostState(val online: Boolean, val timestamp: Long)

class SparkplugHandler(private val nodeType : String,
                       private val nodeId : String,
                       private val groupId : String,
                       private val hostId : String,
                       private val broker : String,
                       private val metrics : List<MetricDefinition>,
                       private val drivers : Drivers,
                       private val seqPovider : BdSeqProvider,
                       private val backgroundScope : CoroutineScope
) : NoCoLogging {

    private val decodedUri = URI("mqtt://$broker:1883")
    private val hostName = decodedUri.host
    private val port = decodedUri.port

    private val nBirthTopic = "spBv1.0/$groupId/NBIRTH/$nodeId"
    private val nDeathTopic = "spBv1.0/$groupId/NDEATH/$nodeId"
    private val nDataTopic = "spBv1.0/$groupId/NDATA/$nodeId"
    private val nCmdTopic = "spBv1.0/$groupId/NCMD/$nodeId"
    private val stateTopic = "spBv1.0/STATE/$hostId"

    private val decoder = SparkplugBPayloadDecoder()

    private var bdSequence = seqPovider.nextBdSeq()
    private var seqNr = 0L

    private val stateFlow : MutableStateFlow<NodeState> = MutableStateFlow(NodeState.Offline)

    private lateinit var client: Mqtt3AsyncClient
    private val metricDataTypeMap = MetricDataTypeMap()
    private var connected = false
    
    private var nodeState: NodeState = NodeState.Offline
        get() {
            return field
        }
        set(state) {
            field = state
            stateFlow.tryEmit(state)
        }

    val state : StateFlow<NodeState>
        get() = stateFlow.asStateFlow()

    suspend fun run() = coroutineScope{

        logger.info { "Starting SparkplugService.." }

        //drivers.registerDrivers(nodeDefinition)
        registerMetrics(metrics)

        logger.info { "Starting Sparkplug node for groupId = '$groupId" }

        logger.info { "Start metrics listener" }
        backgroundScope.launch {
            drivers.subscribe().collect { ms ->
                if (connected) {
                    // Create and send the NDATA
                    val payload = createNdata(ms)

                    logger.info { "Publishing NDATA on topic $nDataTopic" }
                    val publishMessage = Mqtt3Publish.builder()
                        .topic(nDataTopic)
                        .qos(MqttQos.AT_MOST_ONCE)
                        .retain(false)
                        .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                        .build()
                    client.publish(publishMessage)
                }
            }
        }

        logger.info { "Start drivers listener" }
        backgroundScope.launch(Dispatchers.IO) {
            drivers.startDriversListener()
        }

        logger.info { "Connect to broker" }
        client = Mqtt3Client.builder()
            .serverHost(hostName)
            .serverPort(port)
            .identifier(nodeId)
            .willPublish()
            .topic(nDeathTopic)
            .payload(SparkplugBPayloadEncoder().getBytes(createNdeath(bdSequence), true))
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(false)
            .applyWillPublish()
            .addConnectedListener { ctx -> handleConnected() }
            .addDisconnectedListener { handleDisconnected() }
            .addDisconnectedListener { ctx ->
                (ctx.reconnector as Mqtt3ClientReconnector)
                    .reconnect(true) // always reconnect (includes calling disconnect)
                    .delay(2L * ctx.reconnector.attempts, TimeUnit.SECONDS); // linear scaling delay
            }
            .addDisconnectedListener { ctx ->
                (ctx.reconnector as Mqtt3ClientReconnector)
                    .connectWith()
                    .cleanSession(true)
                    .keepAlive(30)
                    .willPublish()
                    .topic(nDeathTopic)
                    .payload(SparkplugBPayloadEncoder().getBytes(createNdeath(bdSequence), true))
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .retain(false)
                    .applyWillPublish()
                    .applyConnect();
            }

            .build().toAsync()
        client.toAsync().connect()
    }

    /**
     * registerMetrics()
     *
     * Register the metrics to allow Tahu to encode and decode them
     * @param definition the node type definition
     */
    private fun registerMetrics(metrics: List<MetricDefinition>) {
        for (m in metrics) {
            logger.info { "Registering metric - name ${m.metricName} type  ${m.type}" }
            metricDataTypeMap.addMetricDataType(m.metricName, m.type)
        }
    }

    /**
     * handleConnected()
     *
     * Handle MQ/TT connection. Subscribe to the host state
     */
    private fun handleConnected() {
        logger.info { "MQTT - connected" }
        connected = true

        //Subscribe to STATE messages for the host
        logger.info { "Subscribing state changes on $stateTopic" }
        client
            .toAsync()
            .subscribeWith()
            .topicFilter(stateTopic)
            .callback { m -> backgroundScope.launch { handleState(m) } }
            .send()

        nodeState = NodeState.WaitingForHostState
    }

    /**
     * handleDisconnected()
     *
     * Handle MQ/TT disconnect. Stop the asynchronous updates, and
     * increment the bdSeq ready for the nect connection
     */
    private fun handleDisconnected() {
        if (nodeState != NodeState.ConnectingToBroker) {
            logger.info { "MQTT - disconnected" }
            client.toAsync().unsubscribeWith().topicFilter(nCmdTopic)
            backgroundScope.launch { drivers.stopUpdates() }
            bdSequence = seqPovider.nextBdSeq()
            seqNr = 0
            nodeState = NodeState.ConnectingToBroker
        }
    }

    /**
     * handleState()
     *
     * Handle the host state message. This can result in a change of the node state.
     * If the host is going from offline to online the node will also come online
     * (send NBIRTH, listen for NCMD and start publishing updates)
     * If the host goes offline the node will go offline too (stop publishing updates)
     *
     * @param msg the raw Status message
     */
    private suspend fun handleState(msg: Mqtt3Publish) = coroutineScope {
        val data = String(msg.payloadAsBytes)

        logger.info { "Received state change $data" }
        val state = Json.decodeFromString<HostState>(data)

        logger.info { "Current node state = ${nodeState.name} host stare = ${state.online}" }
        if (nodeState == NodeState.WaitingForHostState && state.online) {
            //nodeStateManager.changeState(state, onOnline = {

            //Subscribe to NCMD messages for this mode
            logger.info { "Subscribing all commands on $nCmdTopic" }
            client
                .subscribeWith()
                .topicFilter(nCmdTopic)
                .callback { m -> backgroundScope.launch { handleNCMD(m) } }
                .send()

            // Start the metrics listener to publish asynchronous NDATA messages
            // and instruct the drivers service to listen for events from the drivers

            // Create the NBirth
            val payload = backgroundScope.async { createNbirth(bdSequence) }.await()

            logger.info { "Publishing NBIRTH on topic $nBirthTopic" }
            val publishMessage = Mqtt3Publish.builder()
                .topic(nBirthTopic)
                .qos(MqttQos.EXACTLY_ONCE)
                .retain(false)
                .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                .build()
            client.publish(publishMessage)

            // Start publishing asyncronous updates from the drivers
            logger.info { "Start async readers" }
            backgroundScope.launch { drivers.startUpdates() }

            nodeState = NodeState.Online

        } else if (nodeState == NodeState.Online && !state.online) {
            client.toAsync().unsubscribeWith().topicFilter(nCmdTopic)
            backgroundScope.launch  { drivers.stopUpdates() }
            bdSequence - seqPovider.nextBdSeq()
            seqNr = 0
            nodeState = NodeState.WaitingForHostState
        }
    }

    /**
     * handleNCMD()
     *
     * Decode the NCMD message and use the drivers to write them to the metric
     * implementations
     *
     * @param msg the raw message
     */
    private suspend fun handleNCMD(msg: Mqtt3Publish) = coroutineScope {
        logger.info { "NCMD received on topic ${msg.topic}" }
        try {
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
            val metrics = payload.metrics
            metrics.forEach { m ->
                logger.info { "Received Metric ${m.name}" }
                drivers.writeMetric(m.name, m.metricValue())
            }
        } catch (ex: Exception) {
            logger.error { ex.message }
        }
    }

    /**
     * createNbirth()
     *
     * Create an NBIRTH message by reading akk the metrics from all drivers and
     * assembling them into a payload. The payload included the birth/death
     * sequence nr (bdSeq) metric
     *
     * @param bdSequence the current bdSequence number
     * @return the NBIRTH payload
     */
    private suspend fun createNbirth(bdSequence: Long): SparkplugBPayload = coroutineScope {
        logger.info { "Creating NBIRTH with bdSequance $bdSequence" }
        val metrics = arrayListOf(
            MetricValue(MetricDataType.Int64, bdSequence, Date()).toMetric("bdSeq")
        )
        metrics.addAll(
            async { drivers.readAllMetrics() }
                .await()
                .map { m -> m.value.toMetric(m.metricName) }
        )
        if (logger.isDebugEnabled()) {
            metrics.forEach { m ->
                logger.info { "NBIRTH metric = ${m.name} value = ${m.value}" }
            }
        }
        return@coroutineScope SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetrics(metrics)
            .setTimestamp(Date())
            .setSeq(0)
            .setUuid(nodeType)
            .createPayload()
    }

    /**
     * createNdeath()
     *
     * Create an NDEATH mwssage tpo be added to the MQ/TT connection request.
     * The payload included the birth/death sequence nr (bdSeq) metric
     *
     * @param bdSequence the current bdSequence number
     * @return the NDEATH payload
     */
    private fun createNdeath(bdSequence: Long): SparkplugBPayload {
        logger.info { "Creating NDEATH with bdSequance $bdSequence" }
        val metric = MetricValue(
            type = MetricDataType.UInt64,
            BigInteger.valueOf(bdSequence),
            timestamp = Date()
        ).toMetric("bdSeq")
        val payload = SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetric(metric)
            .setTimestamp(Date())
            .setUuid(nodeType)
            .createPayload()
        return payload
    }

    /**
     * createNdata()
     *
     * @param metricValues list of MetricNameValue
     * @param seqNr the current message seqNr
     * @return
     */
    private fun createNdata(metricValues: List<MetricNameValue>): SparkplugBPayload {
        val metrics = metricValues.map { m -> m.toMetric() }
        return SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetrics(metrics)
            .setTimestamp(Date())
            .setSeq(seqNr++)
            .setUuid(nodeType)
            .createPayload()
    }
}