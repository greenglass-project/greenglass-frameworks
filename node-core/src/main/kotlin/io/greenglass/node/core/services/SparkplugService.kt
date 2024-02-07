/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.services

import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit

import kotlinx.coroutines.*

import org.eclipse.tahu.message.SparkplugBPayloadDecoder
import org.eclipse.tahu.message.SparkplugBPayloadEncoder
import org.eclipse.tahu.message.model.*

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import io.github.oshai.kotlinlogging.KotlinLogging

import io.greenglass.sparkplug.datatypes.MetricValue
import io.greenglass.sparkplug.datatypes.metricValue
import io.greenglass.sparkplug.models.NodeType

import io.greenglass.node.core.services.DriversService.Companion.drivers
import io.greenglass.node.core.models.NodeDefinition
import io.greenglass.node.core.services.PersistenceService.Companion.persistence
import io.greenglass.sparkplug.datatypes.MetricNameValue
import org.eclipse.tahu.model.MetricDataTypeMap
import java.math.BigInteger

class SparkplugService private constructor() {

    private val logger = KotlinLogging.logger {}
    private val decoder = SparkplugBPayloadDecoder()

    private lateinit var uri : String
    private lateinit var groupId : String
    private lateinit var hostId : String
    private lateinit var nodeId : String
    private lateinit var nodeDefinition: NodeDefinition
    private lateinit var nBirthTopic : String
    private lateinit var nDeathTopic : String
    private lateinit var nDataTopic : String
    private lateinit var nCmdTopic : String
    private lateinit var stateTopic: String
    private lateinit var nodeType : String

    private var bdSequence = nextBdSeq()

    private lateinit var client : Mqtt3AsyncClient
    private val metricDataTypeMap =  MetricDataTypeMap()

    private var firstConnection = true
    private var connected = false
    private var nDataSeqNr = 0L

    suspend fun start(uri: String,
                      groupId: String,
                      hostId: String,
                      nodeId : String,
                      nodeDefinition: NodeDefinition) {


        logger.debug { "Starting SparkplugService.." }

        this.uri = uri
        this.groupId = groupId
        this.hostId = hostId
        this.nodeId = nodeId
        this.nodeDefinition = nodeDefinition

        nBirthTopic = "spBv1.0/$groupId/NBIRTH/$nodeId"
        nDeathTopic = "spBv1.0/$groupId/NDEATH/$nodeId/"
        nDataTopic = "spBv1.0/$groupId/NDATA/$nodeId"
        nCmdTopic = "spBv1.0/$groupId/NCMD/$nodeId"
        stateTopic = "spBv1.0/STATE/$hostId"

        nodeType = nodeDefinition.type

        val d = drivers
        d.registerMetrics(nodeDefinition)
        registerMetrics(nodeDefinition)

        val decodedUri = URI(uri)
        val hostName = decodedUri.host
        val port = decodedUri.port

        logger.debug { "Starting Sparkplug node for groupId = '$groupId" }
        client = Mqtt3Client.builder()
            .identifier(nodeId)
            .serverHost(hostName)
            .serverPort(port)
            .willPublish()
            .topic(nDeathTopic)
            .payload(SparkplugBPayloadEncoder().getBytes(createNdeath(bdSequence), true))
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(true)
            .applyWillPublish()
            .automaticReconnect()
            .initialDelay(30, TimeUnit.SECONDS)
            .maxDelay(30, TimeUnit.SECONDS)
            .applyAutomaticReconnect()
            .addDisconnectedListener {
                // create a new birth/death sequence number
                if(connected) {
                    bdSequence = nextBdSeq()
                    connected = false
                    runBlocking { handleDisconnect() }
                    logger.debug { "Disconnection event - next sequence = $bdSequence" }
                }
            }
            .addConnectedListener {
                logger.debug { "Connection event" }
                connected = true
                runBlocking { handleConnect() }
            }
            .buildAsync()
        logger.debug { "Connecting to $hostName $port " }
        client.connect()

        logger.debug { "Starting the send-metrics listener " }
    }

    private fun registerMetrics(definition : NodeType) {
        for(m in definition.metrics) {
            logger.debug { "Registering metric - name ${m.metricName} type  ${m.type}"}
            metricDataTypeMap.addMetricDataType(m.metricName, m.type)
        }
    }

    private suspend fun handleNCMD(msg : Mqtt3Publish) = coroutineScope {
        logger.debug { "NCMD received on topic ${msg.topic}"}
        try {
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
            val metrics = payload.metrics
            metrics.forEach { m ->
                logger.debug { "Received Metric ${m.name}" }
                drivers.writeMetric(m.name, m.metricValue())
                    //.equals(true)) {
                    //createNdata(listOf(MetricNameValue(m.name,m.metricValue())), nDataSeqNr++)
                    //logger.debug { "Publishing NDATA on topic $nDataTopic" }
                    //val publishMessage = Mqtt3Publish.builder()
                    //    .topic(nDataTopic)
                    //    .qos(MqttQos.EXACTLY_ONCE)
                    //    .retain(false)
                    //    .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                    //    .build()
                    //client.publish(publishMessage)
                //}
            }
        } catch (ex:Exception) {
            logger.error { ex.message }
        }
    }

    private fun handleState(msg : Mqtt3Publish) {

    }

    private suspend fun createNbirth(bdSequence : Long) : SparkplugBPayload = coroutineScope {
        val metrics = arrayListOf(
            MetricValue(MetricDataType.Int64, bdSequence, Date()).toMetric("bdSeq")
        )
        metrics.addAll(
            async { drivers.readAllMetrics() }
                .await()
                .map { m -> m.value.toMetric(m.metricName) }
        )
        return@coroutineScope SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetrics(metrics)
            .setTimestamp(Date())
            .setSeq(bdSequence)
            .setUuid(nodeType)
            .createPayload()
    }

    private fun createNdeath(bdSequence : Long) : SparkplugBPayload{
        val metric = MetricValue(
            type = MetricDataType.UInt64,
            BigInteger.valueOf(bdSequence),
            timestamp = Date()
        ).toMetric("bdSeq")
        val payload = SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetric(metric)
            .setTimestamp(Date())
            .setSeq(0)
            .setUuid(nodeType)
            .createPayload()
        logger.debug { "NDEATH payload $payload"}
        return payload
    }

    private fun createNdata(metricValues : List<MetricNameValue>, seqNr : Long) : SparkplugBPayload{
        val metrics = metricValues.map { m -> m.toMetric() }
        return SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetrics(metrics)
            .setTimestamp(Date())
            .setSeq(seqNr)
            .setUuid(nodeType)
            .createPayload()
    }

    private suspend fun metricsListener() {
       drivers.subscribe().collect { ms ->
            if(connected) {
                // Create and send the NDATA
                val payload = createNdata(listOf(ms), nDataSeqNr++)
                logger.debug { "Publishing NDATA on topic $nDataTopic" }
                val publishMessage = Mqtt3Publish.builder()
                    .topic(nDataTopic)
                    .qos(MqttQos.EXACTLY_ONCE)
                    .retain(false)
                    .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                    .build()
                client.publish(publishMessage)
            }
        }
    }
    
    private suspend fun handleConnect() = coroutineScope {
        logger.debug { "MQTT - connected" }
        connected = true

        // Create and send the NBIRTH
        val payload = createNbirth(bdSequence)

        logger.debug { "Publishing NBIRTH on topic $nDataTopic metrics $payload "} //${payload.metrics.map {m -> m.name}.joinToString(",")}"}
        //payload ${payload}"}
        val publishMessage = Mqtt3Publish.builder()
            .topic(nBirthTopic)
            .qos(MqttQos.EXACTLY_ONCE)
            .retain(false)
            .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
            .build()
        client.publish(publishMessage)

        // If first connection set-up the standard subscriptions
        // (these will survive a dropped connection so only need to be done once)
        if (firstConnection) {
            //Subscribe to NCMD messages for this mode
            logger.debug { "Subscribing all commands on $nCmdTopic" }
            client.subscribeWith().topicFilter(nCmdTopic).callback { m -> runBlocking { handleNCMD(m) } }.send()

            //Subscribe to STATE messages for the host
            logger.debug { "Subscribing state changes on $stateTopic" }
            client.subscribeWith().topicFilter(stateTopic).callback { m -> handleState(m) }.send()
            firstConnection = false;

            // Start the metrics listener to publish asynchronous NDATA messages
            // and instruct the drivers service to listen to the drivers for events
            CoroutineScope(Dispatchers.Default).launch { metricsListener() }
            drivers.startDriversListener()

        } else {
            logger.debug { "Reconnection"}
        }

        // Start the driver async tasks
        drivers.startAsyncReaders()
    }

    private suspend fun handleDisconnect() {
        drivers.stopAsyncReaders()
    }

    private fun nextBdSeq() : Long {
        var nextBdSeq = 0L
        val bdSeq = persistence.getLong("bdSeq")
        if(bdSeq != null)
            nextBdSeq = bdSeq + 1
        persistence.setLong("bdSeq", nextBdSeq)
        return nextBdSeq
    }

    companion object {
        @Volatile
        private var instance: SparkplugService? = null

        val sparkplug : SparkplugService
            get() {
                if (instance == null) {
                    synchronized(this) {
                        if (instance == null) {
                            instance = SparkplugService()
                        }
                    }
                }
                return instance!!
            }
    }
}
