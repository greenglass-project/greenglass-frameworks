package io.greenglass.node.core.devicedriver

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.sparkplug.datatypes.MetricNameValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import io.greenglass.sparkplug.datatypes.MetricValue
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

abstract class DeviceDriver(val name : String, val metrics : Map<String, String>, val config : DriverConfig) {

    private val logger = KotlinLogging.logger {}

    private val functions: Map<String, DriverFunction> =
        this::class.nestedClasses
            .filter { nc -> nc.isSubclassOf(DriverFunction::class)}
            .associate { fc ->  functionName(fc) to instantiate(fc)}

    private fun lookupFunction(function : String) : DriverFunction? {
        logger.debug { "Lookup function $function"}
        return functions[function]
    }

    private fun functionName(func : KClass<*>) =
        checkNotNull(func.simpleName?.lowercase(Locale.getDefault()))

    private fun instantiate(func : KClass<*>) : DriverFunction {

        logger.debug { "Instantiating function ${func.qualifiedName}"}

        metrics.forEach { e -> logger.debug { "${e.key} -> ${e.value}" } }

        val functionName = functionName(func)
        val metric = checkNotNull(metrics[functionName], lazyMessage = {"No metric found for function '$functionName'"})

        logger.debug { "Driver '$name' found function '$functionName' implementing metric '$metric'"}
        val constructor = checkNotNull(func.primaryConstructor)
        logger.debug { "Instantiating function"}
        return constructor.call(this, metric) as DriverFunction
    }

    protected val valueFlow = MutableSharedFlow<MetricNameValue>(1)

    // Interface for the Drivers Service to use
    val metricUpdatesFlow: SharedFlow<MetricNameValue>
        get() = valueFlow.asSharedFlow()

    suspend fun startAsyncReadValue(function : String) = checkNotNull(lookupFunction(function)?.startAsyncRead())
    suspend fun stopAsyncReadValue(function : String) = checkNotNull(lookupFunction(function)?.startAsyncRead())
    suspend fun readValue(function : String) : MetricValue = checkNotNull(lookupFunction(function)?.readValue())
    suspend fun writeValue(function : String, value: MetricValue)  = checkNotNull(lookupFunction(function)?.writeValue(value))
}