package io.greenglass.host.control.controlprocess.registry

import kotlin.reflect.KClass
import kotlinx.coroutines.CloseableCoroutineDispatcher

import io.greenglass.host.application.error.NotFoundException
import io.greenglass.host.control.controlprocess.models.ProcessModel
import io.greenglass.host.control.controlprocess.providers.MetricProvider
import io.greenglass.host.control.controlprocess.providers.ProcessStateProvider
import io.greenglass.host.control.controlprocess.providers.SetpointProvider
import io.greenglass.host.control.controlprocess.process.Process
import io.greenglass.host.sparkplug.SparkplugService

import kotlinx.coroutines.ExperimentalCoroutinesApi

open class ProcessRegistry {

    @Throws(NotFoundException::class)
    open fun modelClassForName(name : String) : KClass<out ProcessModel> {
        throw NotImplementedError()
    }

    /**
     * processForName
     *
     * @param name
     * @param systemInstance
     * @param processModel
     * @param metricProvider
     * @param setpointProvider
     * @param processStateProvider
     * @param sparkplugService
     * @return the process instance
     * @throws NotFoundException
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Throws(NotFoundException::class)
    open fun processForName(name : String,
                            instanceId : String,
                            processModel : ProcessModel,
                            metricProvider : MetricProvider,
                            setpointProvider : SetpointProvider,
                            processStateProvider : ProcessStateProvider,
                            sparkplugService: SparkplugService,
                            dispatcher : CloseableCoroutineDispatcher
    ) : Process {
        throw NotImplementedError()
    }
}