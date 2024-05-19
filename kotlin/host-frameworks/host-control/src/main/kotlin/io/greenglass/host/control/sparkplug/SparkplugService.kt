/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.sparkplug

import java.net.URI
import java.util.*

import com.fasterxml.jackson.databind.ObjectMapper
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import io.github.oshai.kotlinlogging.KotlinLogging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.concurrent.TimeUnit

import org.eclipse.tahu.message.SparkplugBPayloadDecoder
import org.eclipse.tahu.message.SparkplugBPayloadEncoder
import org.eclipse.tahu.message.model.MetricDataType
import org.eclipse.tahu.message.model.SparkplugBPayload
import org.eclipse.tahu.message.model.StatePayload
import org.eclipse.tahu.message.model.Topic
import org.eclipse.tahu.model.MetricDataTypeMap

import io.greenglass.host.control.sparkplug.datatypes.MetricValue
import io.greenglass.host.control.sparkplug.datatypes.metricValue
import io.greenglass.host.control.sparkplug.models.Metric
import io.greenglass.host.control.sparkplug.models.NodeType
import io.greenglass.host.control.datatypes.*
import io.greenglass.host.control.sparkplug.datatypes.MetricIdentifier
import io.greenglass.host.control.sparkplug.models.MetricDirection
import kotlinx.coroutines.coroutineScope


class SparkplugService(

    private val uri: String,
    private val groupId: String,
    private val hostAppId: String,
    private val backgroundScope : CoroutineScope
) {
    private class MetricContext(
        val type : MetricDataType,
        val metricName : String,
        val handler : suspend (String, String, MetricValue) -> Unit
    )

    private class NodeContext(
        var nodeName : String,
        var nodeId : String?,
        var type : String,
        var bdSeq : BigInteger,
        var handler : suspend (NodeStateValue) -> Unit,
        val metrics : HashMap<String, MetricContext>
    )

    /// Node name -> node-type
    private val nodeTypes : HashMap<String, NodeType> = hashMapOf()

    /// Node-id -> Node-name
    private val nodeIdToContext : HashMap<String, NodeContext> = hashMapOf()

    // Node-name -> node context
    private val nodeNameToContext : HashMap<String, NodeContext> = hashMapOf()

    private val nodeTypeMap : HashMap<String, Map<String, Metric>> = hashMapOf()

    //------ legacy
    private val logger = KotlinLogging.logger {}
    private val metricDataTypeMap : MetricDataTypeMap = MetricDataTypeMap()

    private val decoder = SparkplugBPayloadDecoder()
    private val objectMapper = ObjectMapper()
    private val client : Mqtt3AsyncClient

    private var connected = false
    private var hostState = true

    init {
        val decodedUri = URI(uri)
        val hostName = decodedUri.host
        val port = decodedUri.port

        client = Mqtt3Client.builder()
            .serverHost(hostName)
            .serverPort(port)
            .willPublish()
            .topic("spBv1.0/STATE/$hostAppId")
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

    // =======================================================================================
    // Public API
    // + addNodeType()
    // + bindNode()
    // + subscribeToNode()
    // + subScribeToMetrics()
    // + publishToMetric()
    // =======================================================================================

    fun addNodeType(nodeType : NodeType) {
        nodeTypes[nodeType.type] = nodeType
        nodeType.metrics.forEach { m ->
            metricDataTypeMap.addMetricDataType(m.metricName, m.type)
        }
    }

    fun subscribeToNode(nodeName : String, type : String, handler : (NodeStateValue) -> Unit) {
        logger.debug { "addNode() $nodeName type $type" }
        val ctx = NodeContext(
            nodeName = nodeName,
            nodeId = null,
            type = type,
            bdSeq = BigInteger.valueOf(-1L),
            handler = handler,
            metrics = hashMapOf()
        )
        nodeNameToContext[nodeName] = ctx
    }

    fun subScribeToMetrics(metrics : List<MetricIdentifier>, handler : (String, String, MetricValue) -> Unit) {
        metrics.forEach { m ->
            val nodeCtx = checkNotNull(nodeNameToContext[m.nodeName])
            val ctx = MetricContext(
                type = m.type,
                metricName = m.metricName,
                handler = handler
            )
            nodeCtx.metrics[m.metricName] = ctx
        }
    }

    fun bindNode(nodeName : String, nodeId : String?) {
        logger.debug { "bindNode() $nodeName -> $nodeId" }
        val ctx = checkNotNull(nodeNameToContext[nodeName])

        // If this node is already bound - unbind it
        if (ctx.nodeId != null) {
            logger.debug { "Node $nodeName unbinding from ${ctx.nodeId }" }
            unSubscribeFromNbirth("spBv1.0/$groupId/NBIRTH/$nodeId")
            unSubscribeFromNdata("spBv1.0/$groupId/NDATA/$nodeId")
            unSubscribeFromNdeath("spBv1.0/$groupId/NDEATH/$nodeId")
            nodeIdToContext.remove(nodeId)
        }

        // If the passed node-id is not null, bind it
        if (nodeId != null) {
            logger.debug { "Node $nodeName binding to $nodeId" }

            ctx.nodeId = nodeId
            nodeIdToContext[nodeId] = ctx

            // Start the subscriptions for the node
            subscribeToNbirth("spBv1.0/$groupId/NBIRTH/$nodeId")
            subscribeToNdata("spBv1.0/$groupId/NDATA/$nodeId")
            subscribeToNdeath("spBv1.0/$groupId/NDEATH/$nodeId")
        }
    }

    fun publishToMetric(nodeName: String, metricName : String, value : MetricValue) {
        if(value.value != null) {
            logger.debug { "Publishing to metric node $nodeName metric $metricName" }
            val node = checkNotNull(nodeNameToContext[nodeName])
            if(node.nodeId != null) {

                val topic = "spBv1.0/$groupId/NCMD/${node.nodeId}"
                val payload = SparkplugBPayload(
                    Date(),
                    listOf(value.toMetric(metricName))
                )
                logger.debug("Publishing to $topic metric $metricName payload $payload")

                val publishMessage = Mqtt3Publish.builder()
                    .topic(topic)
                    .qos(MqttQos.EXACTLY_ONCE)
                    .retain(false)
                    .payload(SparkplugBPayloadEncoder().getBytes(payload, true))
                    .build()

                client.publish(publishMessage)
            }
        } else
            logger.debug { "Node '$nodeName' : Not publishing null metric '$metricName" }
    }

    // ====================================================================================
    //  Internal functions
    // ====================================================================================

    private fun subscribeToNbirth(nBirthtopic : String ) {
        logger.debug {"Subscribing to topic $nBirthtopic" }
        client.subscribeWith().topicFilter(nBirthtopic).callback { msg ->

            // Handle NBIRTH
            logger.debug { "Received NBIRTH on topic ${msg.topic}" }
            val topic = Topic.parseTopic(msg.topic.toString())
            val eonId = topic.edgeNodeId

            // Decode the payload
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
            //logger.debug { "Message = $payload"}

            val nodeCtx = nodeIdToContext[topic.edgeNodeId]

            // If the node is bound set its state to online
            if(nodeCtx != null) {
                logger.debug { "PUBLISHING Node ${topic.edgeNodeId} online" }
                backgroundScope.launch {
                    nodeCtx.handler(NodeStateValue(nodeCtx.nodeName, NodeState.Online))
                }

                // Set the Birth-death sq in the node context and
                // update publish the metric values contained in the payload
                payload.metrics.forEach { metric ->
                    logger.debug { "Found metric ${metric.name} value ${metric.value}" }
                    if (metric.name == "bdSeq")
                        nodeCtx.bdSeq = metric.metricValue().uint64
                    else {
                        val ctx = nodeCtx.metrics[metric.name]
                        if(ctx != null) {
                            logger.debug { "Publishing value..." }
                            backgroundScope.launch {
                                ctx.handler(nodeCtx.nodeName, metric.name, metric.metricValue())
                            }
                        }
                    }
                }
            }
        }.send()
    }

    private fun unSubscribeFromNbirth(nBirthtopic : String) {
        logger.debug {"Unsubscribing from topic $nBirthtopic" }
        client.unsubscribeWith().topicFilter(nBirthtopic)
    }

    private fun subscribeToNdata(nDatatopic : String) {
        logger.debug { "Subscribing to topic $nDatatopic" }
        client.subscribeWith().topicFilter(nDatatopic).qos(MqttQos.AT_MOST_ONCE).callback { msg ->

            //logger.debug { "Received message on topic ${msg.topic}" }
            val topic = Topic.parseTopic(msg.topic.toString())
            val nodeId = topic.edgeNodeId
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)
           // logger.debug { "Message = $payload"}

            val nodeCtx = checkNotNull(nodeIdToContext[nodeId])
            payload.metrics.forEach { metric ->
                logger.debug { "Found metric ${metric.name} value ${metric.value}" }
                val ctx = nodeCtx.metrics[metric.name]
                if (ctx != null) {
                    logger.debug { "Publishing value..." }
                    backgroundScope.launch {
                        ctx.handler(nodeCtx.nodeName, metric.name, metric.metricValue())
                    }
                }
            }
        }.send()
    }

    private fun unSubscribeFromNdata(nDatatopic : String) {
        logger.debug {"Unsubscribing from topic $nDatatopic" }
        client.unsubscribeWith().topicFilter(nDatatopic)
    }

    private fun subscribeToNdeath(nDeathtopic : String)  {
        logger.debug { "Subscribing to NDEATH topic $nDeathtopic" }
        client.subscribeWith().topicFilter(nDeathtopic).qos(MqttQos.AT_LEAST_ONCE).callback { msg ->

            // Handle NDEATH
            logger.debug { "Received NDEATH on topic ${msg.topic}" }
            val topic = Topic.parseTopic(msg.topic.toString())
            val payload = decoder.buildFromByteArray(msg.payloadAsBytes, metricDataTypeMap)

            val nodeCtx = nodeIdToContext[topic.edgeNodeId]
            if (nodeCtx != null) {

                // Get the BdSeq metric from the payload, and verify that it matches
                // the value found in the nodeCtx
                // logger.debug { "Message = $payload"}
                if(payload.metricCount == 1 && payload.metrics[0].name == "bdSeq") {
                    if (payload.metrics[0].metricValue().uint64 == nodeCtx.bdSeq) {

                        backgroundScope.launch {
                            nodeCtx.handler(NodeStateValue(nodeCtx.nodeName, NodeState.Offline))
                        }

                        // Set all the metric values to stale
                        nodeCtx.metrics.values.forEach { metricCtx ->
                            suspend {
                                metricCtx.handler(nodeCtx.nodeName, metricCtx.metricName, MetricValue())
                            }
                        }
                    }
                }
            }
        }.send()
    }

    private fun unSubscribeFromNdeath(nDeathtopic : String) {
        logger.debug {"Unsubscribing from topic $nDeathtopic" }
        client.unsubscribeWith().topicFilter(nDeathtopic)
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
