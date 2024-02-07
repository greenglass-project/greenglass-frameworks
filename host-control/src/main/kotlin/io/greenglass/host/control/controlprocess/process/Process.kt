package io.greenglass.host.control.controlprocess.process

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.coroutineContext

import io.greenglass.host.control.controlprocess.models.ProcessModel
import io.greenglass.host.control.controlprocess.providers.MetricProvider
import io.greenglass.host.control.controlprocess.providers.ProcessStateProvider
import io.greenglass.host.control.controlprocess.providers.SetpointProvider
import io.greenglass.host.sparkplug.SparkplugService
import io.greenglass.sparkplug.datatypes.MetricValue

abstract class Process  @OptIn(ExperimentalCoroutinesApi::class) constructor(val instanceId : String,
                                                                             val processModel : ProcessModel,
                                                                             val metricProvider : MetricProvider,
                                                                             val setpointProvider : SetpointProvider,
                                                                             val processStateProvider : ProcessStateProvider,
                                                                             val sparkplugService: SparkplugService,
                                                                             val dispatcher : CloseableCoroutineDispatcher
) {

    protected val pvValueFlows = processModel.processVariables?.associate { pv ->
            Pair(pv.variableId, MutableStateFlow(MetricValue()))
    } ?: mapOf()

    protected val pvSetpointFlows = processModel.processVariables?.associate { pv ->
        Pair(pv.variableId, MutableStateFlow(MetricValue()))
    } ?: mapOf()

    protected var state = false;
    protected val processStateFlow: MutableStateFlow<MetricValue> = MutableStateFlow(MetricValue(state))
    protected lateinit var processScope : CoroutineScope

    @OptIn(ExperimentalCoroutinesApi::class)
    open suspend fun run() {
        processScope = CoroutineScope(coroutineContext)

        val stateFLow = processStateProvider.subscribeToProcessState(instanceId, processModel.processId)
        processScope.launch(dispatcher) {
            stateFLow.collect { s ->
                if(s != state) {
                    state = s
                    processStateFlow.emit(MetricValue(state))
                    if(state)
                        start()
                    else
                        stop()

                }
            }
        }
    }

    abstract suspend fun start()
    abstract suspend fun stop()

    fun supscribePVvalue(variableId : String) : StateFlow<MetricValue> = checkNotNull(pvValueFlows[variableId])
    fun subscribePVsetpoint(variableId : String): StateFlow<MetricValue> = checkNotNull(pvSetpointFlows[variableId])
    fun subscribeProcessState() = processStateFlow.asStateFlow()
}