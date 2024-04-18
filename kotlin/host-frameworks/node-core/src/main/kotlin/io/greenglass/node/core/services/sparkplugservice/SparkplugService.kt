/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.services.sparkplugservice

import java.math.BigInteger
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.lifecycle.Mqtt3ClientReconnector
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish

import io.github.oshai.kotlinlogging.KotlinLogging

import org.eclipse.tahu.message.SparkplugBPayloadDecoder
import org.eclipse.tahu.message.SparkplugBPayloadEncoder
import org.eclipse.tahu.message.model.*
import org.eclipse.tahu.model.MetricDataTypeMap

import io.greenglass.node.core.models.NodeDefinition
import io.greenglass.node.core.models.Settings
import io.greenglass.node.core.services.*
import io.greenglass.sparkplug.datatypes.MetricNameValue
import io.greenglass.sparkplug.datatypes.MetricValue
import io.greenglass.sparkplug.datatypes.metricValue
import io.greenglass.sparkplug.models.NodeType
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * SparkplugService
 *
 * Manages the Sparkplug protocol and connection with the broker
 *
 * @property nodeTypes
 * @property persistence
 * @property drivers
 * @property backgroundScope
 */
class SparkplugService(private val nodeTypes : NodeTypesService,
                       private val persistence : PersistenceService,
                       private val drivers : DriversService,
                       private val webService: WebService,
                       private val backgroundScope : CoroutineScope
    ) {

    private val logger = KotlinLogging.logger {}
    private val decoder = SparkplugBPayloadDecoder()

    private lateinit var uri: String
    private lateinit var groupId: String
    private lateinit var hostId: String
    private lateinit var nodeId: String
    private lateinit var nodeDefinition: NodeDefinition
    private lateinit var nBirthTopic: String
    private lateinit var nDeathTopic: String
    private lateinit var nDataTopic: String
    private lateinit var nCmdTopic: String
    private lateinit var stateTopic: String
    private lateinit var nodeType: String

    private lateinit var decodedUri: URI
    private lateinit var hostName: String
    private var port = 0

    private var bdSequence = nextBdSeq()
    private var seqNr = 0L

    private val stateFlow : MutableStateFlow<NodeState> = MutableStateFlow(NodeState.Offline)

    private val mapper = jacksonObjectMapper()
    private lateinit var client: Mqtt3AsyncClient
    private val metricDataTypeMap = MetricDataTypeMap()
    private var connected = false


    private var nodeState: NodeState = NodeState.Offline
        get() {
            logger.debug { "STATE IS ${field.name}" }
            return field
        }
        set(state) {
            field = state
            stateFlow.tryEmit(state)
            logger.debug { "SETTING STATE ${state.name}" }
        }

    /**
     * initialise()
     *
     * Start the node - tate web service
     */
    suspend fun initialise() {
        backgroundScope.launch {
            val currentStateFlow = stateFlow
                .asStateFlow()
                .map { ns -> ns.value }
                .transform { e -> emit(Json.encodeToString(IntValue(e))) }
                .stateIn(backgroundScope)
            webService.addEventPublisher("/sparkplug/state", currentStateFlow)
        }
    }

    /**
     * registerMetrics()
     *
     * Register the metrics to allow Tahu to encode and decode them
     * @param definition the node type definition
     */
    private fun registerMetrics(definition: NodeType) {
        for (m in definition.metrics) {
            logger.debug { "Registering metric - name ${m.metricName} type  ${m.type}" }
            metricDataTypeMap.addMetricDataType(m.metricName, m.type)
        }
    }

    fun  restart(settings: Settings) {
        if(connected)
            client.disconnect()
    }

    suspend fun stop() {
    }

    /**
     * start()
     *
     * Start the service. Initiate an MQ/TT connection
     *
     * @param uri
     * @param groupId
     * @param hostId
     * @param nodeId
     * @param nodeDefinition
     */
    suspend fun start(settings: Settings) {

        logger.debug { "Starting SparkplugService.." }

        this@SparkplugService.uri = settings.broker
        this@SparkplugService.groupId = settings.groupId
        this@SparkplugService.hostId = settings.hostId
        this@SparkplugService.nodeId = settings.nodeId
        this@SparkplugService.nodeDefinition = nodeTypes.getType(settings.nodeType)

        decodedUri = URI("mqtt://$uri:1883")
        hostName = decodedUri.host
        port = decodedUri.port

        nBirthTopic = "spBv1.0/$groupId/NBIRTH/$nodeId"
        nDeathTopic = "spBv1.0/$groupId/NDEATH/$nodeId"
        nDataTopic = "spBv1.0/$groupId/NDATA/$nodeId"
        nCmdTopic = "spBv1.0/$groupId/NCMD/$nodeId"
        stateTopic = "spBv1.0/STATE/$hostId"

        nodeType = nodeDefinition.type

        drivers.registerDrivers(nodeDefinition)
        registerMetrics(nodeDefinition)

        logger.debug { "Starting Sparkplug node for groupId = '$groupId" }

        logger.debug { "Start metrics listener" }
        backgroundScope.launch {
            drivers.subscribe().collect { ms ->
                if (connected) {
                    // Create and send the NDATA
                    val payload = createNdata(ms)

                    logger.debug { "Publishing NDATA on topic $nDataTopic" }
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

        logger.debug { "Start drivers listener" }
        backgroundScope.launch(Dispatchers.IO) {
            drivers.startDriversListener()
        }

        logger.debug { "Connect to broker" }
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
     * handleConnected()
     *
     * Handle MQ/TT connection. Subscribe to the host state
     */
    private fun handleConnected() {
        logger.debug { "MQTT - connected" }
        connected = true

        //Subscribe to STATE messages for the host
        logger.debug { "Subscribing state changes on $stateTopic" }
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
            logger.debug { "MQTT - disconnected" }
            client.toAsync().unsubscribeWith().topicFilter(nCmdTopic)
            backgroundScope.launch { drivers.stopUpdates() }
            bdSequence = nextBdSeq()
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

        logger.debug { "Received state change $data" }
        val state = Json.decodeFromString<HostState>(data)

        logger.debug { "Current node state = ${nodeState.name} host stare = ${state.online}" }
        if (nodeState == NodeState.WaitingForHostState && state.online) {
            //nodeStateManager.changeState(state, onOnline = {

            //Subscribe to NCMD messages for this mode
            logger.debug { "Subscribing all commands on $nCmdTopic" }
            client
                .subscribeWith()
                .topicFilter(nCmdTopic)
                .callback { m -> backgroundScope.launch { handleNCMD(m) } }
                .send()

            // Start the metrics listener to publish asynchronous NDATA messages
            // and instruct the drivers service to listen for events from the drivers

            // Create the NBirth
            val payload = backgroundScope.async { createNbirth(bdSequence) }.await()

            logger.debug { "Publishing NBIRTH on topic $nBirthTopic" }
            val publishMessage = Mqtt3Publish.builder()
                .topic(nBirthTopic)
                .qos(MqttQos.EXACTLY_ONCE)
                .retain(false)
                .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                .build()
            client.publish(publishMessage)

            // Start publishing asyncronous updates from the drivers
            logger.debug { "Start async readers" }
            backgroundScope.launch { drivers.startUpdates() }

            nodeState = NodeState.Online

        } else if (nodeState == NodeState.Online && !state.online) {
            client.toAsync().unsubscribeWith().topicFilter(nCmdTopic)
            backgroundScope.launch  { drivers.stopUpdates() }
            bdSequence - nextBdSeq()
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
        logger.debug { "NCMD received on topic ${msg.topic}" }
        try {
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
            val metrics = payload.metrics
            metrics.forEach { m ->
                logger.debug { "Received Metric ${m.name}" }
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
        logger.debug { "Creating NBIRTH with bdSequance $bdSequence" }
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
                logger.debug { "NBIRTH metric = ${m.name} value = ${m.value}" }
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
        logger.debug { "Creating NDEATH with bdSequance $bdSequence" }
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

    /**
     * metricsListener()O
     *
     * Subscribe to the drivers service and listen for metrics
     * to send, Create an NDATA message and publish it.
     */
    private suspend fun metricsListener() {

    }

    /**
     * nextBdSeq()
     *
     * Increment the persisted birth/death sequence number
     *
     * @return the sequence number
     */
    private fun nextBdSeq(): Long {
        val bdSeq = persistence.getLong("bdSeq", -1)
        val nextBdSeq = bdSeq + 1
        persistence.setLong("bdSeq", nextBdSeq)
        return nextBdSeq
    }
}