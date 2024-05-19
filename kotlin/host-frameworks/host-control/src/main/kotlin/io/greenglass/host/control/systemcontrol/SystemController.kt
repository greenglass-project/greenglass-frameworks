/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.systemcontrol

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.host.control.datatypes.*
import io.greenglass.host.control.models.MetricVariable
import io.greenglass.host.control.models.SystemDefinition
import io.greenglass.host.control.models.variableidentifiers.EonNodeId
import io.greenglass.host.control.models.variableidentifiers.ProcessVariableIdentifier
import io.greenglass.host.control.process.ProcessEngineClient
import io.greenglass.host.control.sparkplug.SparkplugService
import io.greenglass.host.control.sparkplug.datatypes.MetricIdentifier
import io.greenglass.host.control.sparkplug.models.Metric
import io.greenglass.host.control.sparkplug.models.NodeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SystemController(private val definition : SystemDefinition,
                       private val nodeTypes : List<NodeType>,
                       private val nodeIds : List<EonNodeId>,
                       private val sparkplug : SparkplugService,
                       private val engineClient : ProcessEngineClient,
                       private val coroutineScope: CoroutineScope
) {

    private val logger = KotlinLogging.logger {}


    private val variableFlows: HashMap<Int, MutableStateFlow<VariableValue>> = hashMapOf()
    private val metricInputFlows: HashMap<String, Int> = hashMapOf()
    private val metricOutputFlows: HashMap<String, Int> = hashMapOf()
    private val inputFlows: HashMap<String, Int> = hashMapOf()
    private val outputFlows: HashMap<String, Int> = hashMapOf()

    val nodeStateFlows: HashMap<String, MutableStateFlow<NodeStateValue>> = hashMapOf()
    val nodeIdFlows: HashMap<String, MutableStateFlow<NodeIdValue>> = hashMapOf()

    val inputMetrics: ArrayList<MetricIdentifier> = arrayListOf()
    val outputMetrics: ArrayList<MetricIdentifier> = arrayListOf()
    val inputVariables: ArrayList<ProcessVariableIdentifier> = arrayListOf()
    val outputVariables: ArrayList<ProcessVariableIdentifier> = arrayListOf()
    val metricVariables: ArrayList<MetricVariable> = arrayListOf()

    // Create a lookup from node-name/metric-name to process-id/variableId
    private var metricToVariable: Map<String, MetricVariable>

    // Create a lookup from process-id/variableId to node-name/metric-name
    private var variableToMetric: Map<String, MetricVariable>

    // Create a mapping from nodename to metricVariables
    private var nodeVariableMetrics: Map<String, List<MetricVariable>>

    // create a mapping from node-name to EonNode (including node-type)
    private val nodeNameToEonNode = definition.nodes.associateBy { n -> n.nodeName }

    // Create a mapping from node-type to metric-name to metric
    private val nodeTypeMetricNameToMetric = nodeTypes.map { nt -> Pair(nt.type, nt.metrics) }
        .associate { p -> Pair(p.first, p.second.associateBy { s -> s.metricName }) }

    // Create a mapping from nodename to note type
    private val nodeTypesMap = nodeTypes.associateBy { nt -> nt.type }
    private fun flowKey(first: String, second: String) = "$first/$second"
    private fun flowForKey(map: HashMap<String, Int>, key: String): MutableStateFlow<VariableValue> {
        val stateKey = checkNotNull(map[key], lazyMessage = { "No flow found for $key" })
        return checkNotNull(variableFlows[stateKey], lazyMessage = { "No state flow found for $stateKey" })
    }

    fun inputFlow(processId: String, variableId: String): MutableStateFlow<VariableValue> =
        flowForKey(inputFlows, flowKey(processId, variableId))

    fun outputFlow(processId: String, variableId: String): MutableStateFlow<VariableValue> =
        flowForKey(outputFlows, flowKey(processId, variableId))

    fun outputFlows() = outputFlows.values.map { of -> checkNotNull(variableFlows[of])}

    fun metricInputFlow(nodeName: String, netricName: String): MutableStateFlow<VariableValue> =
        flowForKey(metricInputFlows, flowKey(nodeName, netricName))

    fun metricOutputFlow(nodeName: String, netricName: String): MutableStateFlow<VariableValue> =
        flowForKey(metricOutputFlows, flowKey(nodeName, netricName))

    fun nodeStateFlow(nodeName: String): MutableStateFlow<NodeStateValue> =
        checkNotNull(nodeStateFlows[nodeName], lazyMessage = { "No state flow found for #$nodeName" })

    fun nodeIdFlow(nodeName: String): MutableStateFlow<NodeIdValue> =
        checkNotNull(nodeIdFlows[nodeName], lazyMessage = { "No nodeid flow found for #$nodeName" })

    fun metricToVariable(nodeName: String, metricName: String) =
        checkNotNull(metricToVariable["$nodeName/$metricName"])

    fun variableToMetric(processId: String, variableId: String) =
        checkNotNull(variableToMetric["$processId/$variableId"])

    fun nodeNameToMetricsVariables(nodeName: String) =
        checkNotNull(nodeVariableMetrics[nodeName], lazyMessage = { "No variables found for #$nodeName" })

    fun nodeNameToEonNode(nodeName: String) = checkNotNull(
        nodeNameToEonNode[nodeName],
        lazyMessage = { "No node found for $nodeName" }
    )

    fun nodeNameToNodeType(nodeName: String): NodeType {
        val eon = nodeNameToEonNode(nodeName)
        return checkNotNull(
            nodeTypesMap[eon.type],
            lazyMessage = { "No type found for ${eon.type}" }
        )
    }

    fun nodeTypeMetricNameToMetric(nodeType: String, metricName: String) =
        checkNotNull(nodeTypeMetricNameToMetric[nodeType]?.get(metricName))

    fun nodeNameMetricNameToMetricPair(nodeName: String, metricName: String): Pair<Metric, MetricVariable> {
        val nodeType = nodeNameToEonNode(nodeName)
        return Pair(nodeTypeMetricNameToMetric(nodeType.type, metricName), metricToVariable(nodeName, metricName))
    }

    init {
        definition.nodes.forEach { n ->
            val stateFlow = MutableStateFlow(NodeStateValue(n.nodeName, NodeState.NotBound))
            nodeStateFlows[n.nodeName] = stateFlow
            val nodeIdidFlow = MutableStateFlow(NodeIdValue(n.nodeName, null))
            nodeIdFlows[n.nodeName] = nodeIdidFlow
        }

        definition.processes.forEach { p ->
            // Create the input flows
            p.inputVariables.forEach { iv ->
                val variableFlow = MutableStateFlow(VariableValue(iv.type))
                val variableKey = variableFlow.hashCode()
                variableFlows[variableKey] = variableFlow
                inputVariables.add(ProcessVariableIdentifier(p.processId, iv.variableId))

                val procVarKey = "${p.processId}/${iv.variableId}"
                when {
                    iv.metricFlow != null -> {
                        inputMetrics.add(
                            MetricIdentifier(
                                nodeName = iv.metricFlow.nodeName,
                                metricName = iv.metricFlow.metricName,
                                type = iv.type.tahuType
                            )
                        )
                        metricVariables.add(
                            MetricVariable(
                                nodeName = iv.metricFlow.nodeName,
                                metricName = iv.metricFlow.metricName,
                                processId = p.processId,
                                variableId = iv.variableId
                            )
                        )
                        val metricKey = "${iv.metricFlow.nodeName}/${iv.metricFlow.metricName}"
                        metricInputFlows[metricKey] = variableKey
                        inputFlows[procVarKey] = variableKey
                    }

                    iv.sharedFlow != null -> inputFlows[procVarKey] = variableKey
                    iv.valueProvider != null -> inputFlows[procVarKey] = variableKey
                }
            }
            p.outputVariables.forEach { ov ->
                val procVarKey = "${p.processId}/${ov.variableId}"
                outputVariables.add(ProcessVariableIdentifier(p.processId, ov.variableId))

                when {
                    ov.metricFlow != null -> {
                        outputMetrics.add(
                            MetricIdentifier(
                                nodeName = ov.metricFlow.nodeName,
                                metricName = ov.metricFlow.metricName,
                                type = ov.type.tahuType
                            )
                        )
                        metricVariables.add(
                            MetricVariable(
                                nodeName = ov.metricFlow.nodeName,
                                metricName = ov.metricFlow.metricName,
                                processId = p.processId,
                                variableId = ov.variableId
                            )
                        )
                        val variableFlow = MutableStateFlow(VariableValue(ov.type))
                        val variableKey = variableFlow.hashCode()
                        variableFlows[variableKey] = variableFlow
                        val metricKey = "${ov.metricFlow.nodeName}/${ov.metricFlow.metricName}"
                        metricOutputFlows[metricKey] = variableKey
                        outputFlows[procVarKey] = variableKey
                    }
                }
            }
        }

        // Second pass through output flows to link the shared flows
        definition.processes.forEach { p ->
            p.outputVariables.forEach { ov ->
                val procVarKey = "${p.processId}/${ov.variableId}"
                if (ov.sharedFlow != null) {
                    // find the input flow
                    val inputKey = "${ov.sharedFlow.processId}/${ov.sharedFlow.variableId}"
                    val stateKey =
                        checkNotNull(inputFlows[inputKey], lazyMessage = { "Flow with key $inputKey not found" })
                    outputFlows[procVarKey] = stateKey
                }
            }
        }

        metricToVariable =
            metricVariables.associateBy { mv -> "${mv.nodeName}/${mv.metricName}" }

        // Create a lookup from process-id/variableId to node-name/metric-name
        variableToMetric =
            metricVariables.associateBy { mv -> "${mv.processId}/${mv.variableId}" }

        // Create a mapping from nodename to metricVariables
        nodeVariableMetrics = metricVariables.groupBy({ it.nodeName }, { it })

        logger.debug { "System Controller Initialisation complete" }
    }

    fun start() {
        logger.debug { "STARTING THE VARIABLE FLOWS..."}

        nodeTypes.forEach { nt -> sparkplug.addNodeType(nt) }

        // Subscribe to the nodes,
        // This passes a lambda to be called when
        // the connection state with the node changes
        // (one lambda for all nodes)
        definition.nodes.forEach { n ->
            sparkplug.subscribeToNode(n.nodeName, n.type) { v ->
                val flow = checkNotNull(nodeStateFlows[v.nodeName], lazyMessage = { "Node ${v.nodeName} not found" })
                coroutineScope.launch { flow.emit(v) }
            }
        }

        // subscribe to the incoming metrics.
        // This passes a lambda to be called when
        // a metric value changes. (One lambda for all metrics)
        // The lambda converts the MetricValue to a VariableValue
        // and publishes it on the corresponding flow
        sparkplug.subScribeToMetrics(inputMetrics) { n, m, v ->
            logger.debug { "Input metric variable $n/$m ${v.value}"}
            val variableValue = VariableValue.fromMetricValue(v)
            val variableFlow = flowForKey(metricInputFlows, flowKey(n, m))
            coroutineScope.launch { variableFlow.emit(variableValue) }
        }

        engineClient.start(inputFlows = inputVariables
            .map { i -> Pair(i, inputFlow(i.processId,i.variableId)) }
            .map { p -> IdentifiedFlow(p.first.processId, p.first.variableId, p.second) }
        )

        // Bind the nodes to their node-ids
        definition.nodes.forEach { n ->
            coroutineScope.launch {
                checkNotNull(nodeIdFlows[n.nodeName]).collect { id ->
                    sparkplug.bindNode(id.nodeName, id.nodeId)
                }
            }
        }

        // Listen on each output metric value and publish
        // a metric value using the Sparkplug Service
        outputMetrics.forEach { om ->
            val variableFlow = flowForKey(metricOutputFlows, flowKey(om.nodeName, om.metricName))

            coroutineScope.launch {
                variableFlow.asStateFlow().collect { vv ->
                    sparkplug.publishToMetric(
                        nodeName = om.nodeName,
                        metricName = om.metricName,
                        value = vv.toMetricValue()
                    )
                }
            }
        }


        // Create a single

        // forward all input variable values to the web-socket server
        //inputVariables.forEach { iv ->
        //    val variableFlow = flowForKey(inputFlows, flowKey(iv.processId, iv.variableId))
        //    coroutineScope.launch {
        //        variableFlow.asStateFlow().collect { v ->
        //            wsServer.variableToEngine(iv.processId, iv.variableId, v)
         //       }
        //   }
       // }

        // Publish the current node-name to node-id mapping.
        // This will trigger the node-specific MQ/TT subscriptions
        // amd start the data flow in the system
        nodeIds.forEach { nid ->
            coroutineScope.launch {
                nodeIdFlow(nid.nodeName).emit(NodeIdValue(nid))
            }
        }
    }
}