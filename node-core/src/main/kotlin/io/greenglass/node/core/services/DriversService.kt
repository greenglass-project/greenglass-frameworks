package io.greenglass.node.core.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.devicedriver.DeviceDriver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import io.greenglass.node.core.devicedriver.DriverMetric
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.devicedriver.config.GpioDigitalOutConfig
import io.greenglass.node.core.models.MetricDefinition
import io.greenglass.node.core.models.NodeDefinition
import io.greenglass.sparkplug.datatypes.MetricNameValue
import io.greenglass.sparkplug.datatypes.MetricValue
import io.greenglass.sparkplug.models.MetricDirection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.merge

class DriversService() {

    private val logger = KotlinLogging.logger {}

    data class DriverFunction(val direction: MetricDirection, val function : String, val driver : String)
    data class DriverMetricContext(val metricName: String, val function : String, val driver : String)

    /// Map of driver names to a function that will instantiate the driver
    private val driverRegistry : HashMap<String,(name : String, metrics : Map<String, String>, config : DriverConfig) -> DeviceDriver> = hashMapOf()

    // Map of driver names to driver instances
    private val drivers : HashMap<String, DeviceDriver> = hashMapOf()

    /// Map of driver/function names to metric name
    private val driverFunctions : HashMap<String, HashMap<String,String>> = hashMapOf()

    /// List of metric-publishing flows from the drivers
    private val driverFlows : ArrayList<SharedFlow<MetricNameValue>> = arrayListOf()

    /// Map of metric to driver/function pair
    private val metrics : HashMap<String, DriverFunction> = hashMapOf()

    private val allMetrics : ArrayList<DriverMetricContext> = arrayListOf()

    private val publishMetricsFlow : MutableSharedFlow<MetricNameValue> = MutableSharedFlow(1)

    private fun metricForDriverAndFunction(driver : String, function : String) : String {
        logger.debug { "metricForDriverAndFunction driver = $driver function - $function"}
        return checkNotNull(driverFunctions[driver]?.get(function))
    }

    fun subscribe() : SharedFlow<MetricNameValue> = publishMetricsFlow.asSharedFlow()

    fun registerDriver(type : String, instance : (name : String, metrics : Map<String, String>, config : DriverConfig) -> DeviceDriver) {
        logger.debug { "REGISTERING DRIVER $type"}
        driverRegistry[type] = instance
    }

    fun registerMetrics(definition : NodeDefinition) {
        val driversMap : HashMap<String, String> = hashMapOf();
        val driverMetrics : HashMap<String, HashMap<String,String>> = hashMapOf()
        definition.metrics.forEach {m ->
            val dm = driverMetrics.getOrPut(m.driver) { hashMapOf() }
            dm[m.function] = m.metricName
        }

        for(d in definition.drivers) {
            logger.debug { "Registering metrics - driver name = ${d.name} type = ${d.type}" }
            driversMap[d.name] = d.type

            val init = checkNotNull(driverRegistry[d.type])
            val drvr = init(d.name, checkNotNull(driverMetrics[d.name]), d.config)

            drivers[d.type] = drvr
            driverFlows.add(drvr.metricUpdatesFlow)
        }

        for(m in definition.metrics) {
            logger.debug { "Registering metric - ${m.metricName} -> function ${m.function } driver ${m.driver}"}
            val driverType = checkNotNull(driversMap[m.driver])
            metrics[m.metricName] = DriverFunction(m.direction, m.function, driverType)
            val functions = driverFunctions.getOrPut(driverType) { hashMapOf() }
            functions[m.function] = m.metricName

            allMetrics.add(
                DriverMetricContext(
                    metricName = m.metricName,
                    function = m.function,
                    driver = driverType
                )
            )
        }
    }

    suspend fun startDriversListener() {
        CoroutineScope(Dispatchers.Default).launch {
            merge(*driverFlows.toTypedArray()).collect { m -> publishMetricsFlow.emit(m) }
        }
    }

    suspend fun startAsyncReaders() {
        logger.debug { "Starting driver tasks..."}
        drivers.values.forEach { d ->
            d.metrics.keys.forEach {
                f -> CoroutineScope(Dispatchers.Default).launch { d.startAsyncReadValue(f) }
            }
        }
    }

    suspend fun stopAsyncReaders() {
        logger.debug { "stopping driver tasks..."}
        drivers.values.forEach { d ->  d.metrics.values.forEach { f -> d.stopAsyncReadValue(f) } }
    }

    suspend fun writeMetric(metric : String, value : MetricValue) : Boolean {
        val driverFunc = metrics[metric]
        logger.debug { "Driver = ${driverFunc?.driver} direction = ${driverFunc?.direction?.name}"}
        if(driverFunc != null && driverFunc.direction == MetricDirection.write) {
            drivers.keys.forEach { d -> logger.debug { "DRIVER  $d"} }
            val driver = checkNotNull(drivers[driverFunc.driver])
            val result = driver.writeValue(driverFunc.function, value)
        }
        return false
    }

    suspend fun readAllMetrics() : List<MetricNameValue> = coroutineScope {
        drivers.keys.forEach { d -> logger.debug { "DRIVER  $d"} }
        val allReads = allMetrics.map { m -> async { checkNotNull(drivers[m.driver]).readValue(m.function)} }
        val metrics = awaitAll(*allReads.toTypedArray())
        return@coroutineScope allMetrics.mapIndexed { i, m -> MetricNameValue(m.metricName, metrics[i]) }
    }

    companion object {
        @Volatile
        private var instance: DriversService? = null

        val drivers: DriversService
            get() {
                if (instance == null) {
                    synchronized(this) {
                        if (instance == null) {
                            instance = DriversService()
                        }
                    }
                }
                return instance!!
            }
    }
}


