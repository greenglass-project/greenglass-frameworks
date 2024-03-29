package io.greenglass.host.sparkplug

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.ObjectMapper
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.sparkplug.models.PhysicalNode

import org.eclipse.tahu.message.SparkplugBPayloadDecoder
import org.eclipse.tahu.message.SparkplugBPayloadEncoder
import org.eclipse.tahu.message.model.MetricDataType
import org.eclipse.tahu.message.model.SparkplugBPayload
import org.eclipse.tahu.message.model.StatePayload
import org.eclipse.tahu.message.model.Topic
import org.eclipse.tahu.model.MetricDataTypeMap

import io.greenglass.sparkplug.datatypes.MetricIdentifier
import io.greenglass.sparkplug.datatypes.MetricValue
import io.greenglass.sparkplug.datatypes.metricValue
import java.math.BigInteger

/******************************************************************************
 * Flow<MetricValue>.publishToMetric()
 *
 * @param sparkplug
 * @param nodeId
 * @param name
 *****************************************************************************/
suspend fun SharedFlow<MetricValue>.publishToMetric(sparkplug: SparkplugService,
                                                    nodeId : String,
                                                    name : String,
) : Nothing = collect { v -> sparkplug.publishToMetric(name, nodeId, v) }

/******************************************************************************
 * subscribeToMetric()
 *
 * @param sparkplug
 * @param nodeId
 * @param name
 * @return StateFlow of metric values
 *****************************************************************************/
fun subscribeToMetric(sparkplug: SparkplugService,
                      nodeId : String,
                      name : String) = sparkplug.subscribeToMetric(nodeId, name)

/******************************************************************************
 * subscribeToNode()
 *
 * @param sparkplug
 * @param node
 * @param name
 * @return Boolean StateFlow
 *****************************************************************************/
fun subscribeToNode(sparkplug: SparkplugService,
                    node : PhysicalNode
) = sparkplug.addNode(node)

class SparkplugService(

    private val uri: String,
    private val groupId: String,
    private val hostAppId: String,
    ) {

    private class MetricContext(
        val metricName : String,
        val flow: MutableStateFlow<MetricValue>,
    )

    private class NodeContext(
        val nodeId : String,
        var state : MutableStateFlow<Boolean>,
        var bdSeq : BigInteger,
        val metrics : Map<String, MetricContext>
    )

    private val logger = KotlinLogging.logger {}
    private val metricDataTypeMap : MetricDataTypeMap = MetricDataTypeMap()
    private val nodeContextMap : HashMap<String, NodeContext> = hashMapOf()

    private val decoder = SparkplugBPayloadDecoder()
    private val objectMapper = ObjectMapper()
    private val client : Mqtt3AsyncClient

    private var connected = false;

    init {
        val decodedUri = URI(uri)
        val hostName = decodedUri.host
        val port = decodedUri.port

        client = Mqtt3Client.builder()
            .serverHost(hostName)
            .serverPort(port)
            .willPublish()
            .topic("spBv1.0/STATE/$groupId")
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(true)
            .payload(objectMapper.writeValueAsBytes(StatePayload(false, Date().time)))
            .applyWillPublish()
            .automaticReconnect()
            .initialDelay(30, TimeUnit.SECONDS)
            .maxDelay(30, TimeUnit.SECONDS)
            .applyAutomaticReconnect()
            .addDisconnectedListener {
                logger.debug { "Disconnection event" }
                handleDisconnect()
            }
            .addConnectedListener {
                logger.debug { "Connection event" }
                handleConnect()
            }
            .buildAsync()

        // Add the standard metrics to the datatype map
        metricDataTypeMap.addMetricDataType("bdSeq", MetricDataType.UInt64)
        metricDataTypeMap.addMetricDataType("Node Control/Rebirth", MetricDataType.Boolean)

        logger.debug { "Starting Sparkplug Service for groupId = '$groupId$}" }
        logger.debug { "Connecting to $uri " }
        client.connect()
    }


    /**
     * addNodeDefinition
     *
     * @param nd the physical node definition
     * @return
     */
    fun addNode(node : PhysicalNode) : StateFlow<Boolean> {
        val state = MutableStateFlow(false)
        nodeContextMap[node.nodeId] = NodeContext(
            nodeId = node.nodeId,
            state = state,
            bdSeq = BigInteger.valueOf(-1L),
            metrics = node.metrics.associate { m ->
                m.metricName to MetricContext(
                    metricName = m.metricName,
                    flow = MutableStateFlow(MetricValue())
                )
            }
        )
        // for Tahu
        node.metrics.forEach { m -> metricDataTypeMap.addMetricDataType(m.metricName, m.type) }

        // Start the subscriptions for the node
        subscribeToNbirth(node.nodeId,  "spBv1.0/$groupId/NBIRTH/${node.nodeId}")
        subscribeToNdata(node.nodeId,   "spBv1.0/$groupId/NDATA/${node.nodeId}")
        subscribeToNdeath(node.nodeId, "spBv1.0/$groupId/NDEATH/${node.nodeId}")

        return state.asStateFlow()
    }

    fun removeNode(nodeId : String) {
    }

    fun subscribeToNodeState(nodeId : String) : StateFlow<Boolean> {
        logger.debug { "subscribeToNodeState() nodeId = $nodeId"}

        val nodeCtx = checkNotNull(nodeContextMap[nodeId])
        return nodeCtx.state.asStateFlow()
    }

    fun subscribeToMetric(nodeId : String, name : String) : StateFlow<MetricValue> {
        logger.debug { "subscribeToMetric() nodeId = $nodeId name = $name "}
        val nodeCtx = checkNotNull(nodeContextMap[nodeId])
        val metricCtx = checkNotNull(nodeCtx.metrics[name])
        return metricCtx.flow.asStateFlow()
    }

    fun subscribeToMetric(metric : MetricIdentifier) = subscribeToMetric(metric.nodeId, metric.metricName)

    fun publishToMetric(nodeId : String, name : String, value : MetricValue) {
        if(value.isNotEmpty) {
            KotlinLogging.logger {}.debug { "Publishing to metric $nodeId/$name" }

            val topic = "spBv1.0/$groupId/NCMD/$nodeId"
            val payload = SparkplugBPayload(
                Date(),
                listOf(value.toMetric(name))
            )
            logger.debug("Publishing to $topic metric $name payload $payload")

            val publishMessage = Mqtt3Publish.builder()
                .topic(topic)
                .qos(MqttQos.EXACTLY_ONCE)
                .retain(false)
                .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                .build()

            client.publish(publishMessage)
        } else
            KotlinLogging.logger {}.debug { "Not publishing empty value" }
    }

    //private fun subscriptionCount(nodeId : String) =
    //    nodeContextMap[nodeId]!!.metrics.values.count { m -> m.flow.subscriptionCount.value != 0 }

    private fun subscribeToNbirth(nodeId : String, nBirthtopic : String ) {
        logger.debug { "Subscribing to topic $nBirthtopic" }
        client.subscribeWith().topicFilter(nBirthtopic).callback { msg ->

            // Handle NBIRTH
            logger.debug { "Received message on topic ${msg.topic}" }
            val topic = Topic.parseTopic(msg.topic.toString())
            val eonId = topic.edgeNodeId

            // Decode the payload
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
            //logger.debug { "Message = $payload"}

            // Set the node state to be online
            val nodeCtx = checkNotNull(nodeContextMap[topic.edgeNodeId])
            runBlocking {nodeCtx.state.emit(true) }

            // Set the Birth-death sq in the node context and
            // update publish the metric values contained in the payload
            payload.metrics.forEach { metric ->
                logger.debug { "Found metric ${metric.name}" }
                if (metric.name == "bdSeq") {
                    nodeCtx.bdSeq = metric.metricValue().uint64
                } else {
                    val metricCtx = nodeContextMap[nodeId]?.metrics?.get(metric.name)
                    runBlocking {
                        try {
                            metricCtx?.flow?.emit(metric.metricValue())
                        } catch (_:Exception) {
                            logger.error {"ERROR in $metric.name"}
                        }
                    }
                }
            }
        }.send()
    }

    private fun subscribeToNdata(nodeId : String, nDatatopic : String) {
        logger.debug { "Subscribing to topic $nDatatopic" }
        client.subscribeWith().topicFilter(nDatatopic).callback { msg ->


            logger.debug { "Received message on topic ${msg.topic}" }
            val topic = Topic.parseTopic(msg.topic.toString())
            val eonId = topic.edgeNodeId
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
           // logger.debug { "Message = $payload"}

            val nodeCtx = nodeContextMap[eonId]


            val ms = payload.metrics
            ms.forEach { metric ->
                logger.debug { "Found metric ${metric.name} $metric ${metric.name}"}
                if(nodeCtx != null) {
                    val metricCtx = nodeContextMap[eonId]?.metrics?.get(metric.name)
                    runBlocking {
                        metricCtx?.flow?.emit(metric.metricValue())
                    }
                }
            }
        }.send()
    }

    private fun subscribeToNdeath(nodeId : String, nDeathtopic : String) {
        logger.debug { "Subscribing to topic $nDeathtopic" }
        client.subscribeWith().topicFilter(nDeathtopic).callback { msg ->

            // Handle NDEATH
            logger.debug { "Received message on topic ${msg.topic}" }
            val topic = Topic.parseTopic(msg.topic.toString())
            val eonId = topic.edgeNodeId
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)

            // Get the BdSeq metric from the payload, and verify that it matches
            // the value found in the nodeCtx
           // logger.debug { "Message = $payload"}
            if(payload.metricCount == 1 && payload.metrics[0].name == "bdSeq") {
                val nodeCtx = checkNotNull(nodeContextMap[topic.edgeNodeId])
                if(payload.metrics[0].metricValue().uint64 == nodeCtx.bdSeq) {

                    // Set the node state to be offline
                    runBlocking { nodeCtx.state.emit(false) }

                    // Set all the metric values to stale
                    nodeCtx.metrics.values.forEach { m ->
                        runBlocking { m.flow.emit(MetricValue()) }
                    }
                }
            }
        }.send()
    }

    private fun requestRebirth(nodeId : String) {
        val nCmdTopic = "spBv1.0/$groupId/NCMD/$nodeId"

        val metric =  MetricValue(true).toMetric("Node Control/Rebirth")
        val payload = SparkplugBPayload.SparkplugBPayloadBuilder()
            .addMetrics(listOf(metric))
            .setTimestamp(Date())
            .createPayload()

        logger.debug { "Publishing to $nCmdTopic"}
        logger.debug { payload.toString() }
        val publishMessage = Mqtt3Publish.builder()
            .topic(nCmdTopic)
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(false)
            .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
            .build()
        client.publish(publishMessage)
    }

    private fun handleConnect() {
        logger.debug { "MQTT - connected" }
        connected = true

        // Create the state message and publish it
        //
        val topic = "spBv1.0/STATE/$hostAppId"
        val payload = objectMapper.writeValueAsBytes(StatePayload(true, Date().time))
        logger.debug { "Sending $topic  ${String(payload)} " }

        val publishMessage = Mqtt3Publish.builder()
            .topic(topic)
            .qos(MqttQos.EXACTLY_ONCE)
            .retain(true)
            .payload(payload)
            .build()
        client.publish(publishMessage)
    }

    private fun handleDisconnect() {
        logger.debug { "MQTT - disconnected" }
        connected = false
    }
}
