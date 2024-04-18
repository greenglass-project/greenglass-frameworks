package io.greenglass.node.core.services

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import io.greenglass.node.core.devicedriver.DriverFunctionMetricValue
import io.greenglass.node.core.devicedriver.DriverModule
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.models.NodeDefinition
import io.greenglass.node.sparkplug.Drivers
import io.greenglass.node.sparkplug.datatypes.MetricNameValue
import io.greenglass.node.sparkplug.datatypes.MetricValue
import io.klogging.NoCoLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DriverInfo(val name : String, val type : String)

class DriversService(private val gpio : GpioService,
                     private val persistence : PersistenceService,
                     private val webService : WebService,
                     private val backgroundScope: CoroutineScope
) : Drivers, NoCoLogging {

    /// Driver Registry
    // Map of driver type e.g. "gpio_digital_in" to a function that will instantiate the driver
    // Used statically in the main to register the drivers used in this node
    // The function parameters are:
    // + name - name of this driver eg "green"
    // + config - list of driver config
    private val driverRegistry: HashMap<String, (name: String,
                                                 config: List<DriverConfig>,
                                                 gpio : GpioService,
                                                 persistence : PersistenceService,
                                                 webService : WebService,
                                                 backgroundScope : CoroutineScope
            ) -> DriverModule> = hashMapOf()

    // Drivers
    // Map of driver names e.g "green" to driver instances
    private val drivers: HashMap<String, DriverModule> = hashMapOf()
    private val driversList : ArrayList<DriverInfo> = arrayListOf()
    private val driverFlows: ArrayList<SharedFlow<List<DriverFunctionMetricValue>>> = arrayListOf()

    // Driver Functions to Metrics
    /// Map of metric name to driver-name and function names to metric name
    private val driverFunctionsToMetrics: HashMap<String, HashMap<String, String>> = hashMapOf()

    // Metrics to Driver Functions
    /// Map of Metric name to driver and function pair
    private val metricsTodriverFunctions: HashMap<String, Pair<String, String>> = hashMapOf()

    val publishMetricsFlow = MutableSharedFlow<List<MetricNameValue>>()

    override fun subscribe(): SharedFlow<List<MetricNameValue>> = publishMetricsFlow.asSharedFlow()

    fun registerDriver(type: String,
                       instance: (
                           name: String,
                           config: List<DriverConfig>,
                           gpio : GpioService,
                           persistence : PersistenceService,
                           webService : WebService,
                           backgroundScope : CoroutineScope) -> DriverModule) {

        logger.info { "REGISTERING DRIVER $type" }
        driverRegistry[type] = instance
    }

    suspend fun registerDriversAndMetrics(definition: NodeDefinition) {
        logger.info { "REGISTERING NODE" }

        // ----------------------------------------------------------
        // Register the configured drivers and metrics
        // Register the webService interfaces
        // ----------------------------------------------------------
        for (driver in definition.drivers) {
            val instance = checkNotNull(driverRegistry[driver.type])
            val drv = instance(driver.name, driver.config, gpio, persistence, webService, backgroundScope)
            drv.initialise()
            drivers[driver.name] = drv
            drv.registerSettingsHandler()
            driverFlows.add(drv.valueFlow.asSharedFlow())
            driversList.add(DriverInfo(driver.name, driver.type))
        }

        // Create the maps of functions to metric names for each driver name
        // and driver name and function name to metric name
        definition.metrics.forEach { m ->
            logger.info { "Metric ${m.metricName} -> (${m.driver}, ${m.function})" }
            metricsTodriverFunctions[m.metricName] = Pair(m.driver, m.function)
            val dm = driverFunctionsToMetrics.getOrPut(m.driver) { hashMapOf() }
            dm[m.function] = m.metricName
        }

        // web service to get the list of drivers
        webService.addGet("/drivers") { ctx -> ctx.json(Json.encodeToString(driversList)) }
    }

    /**
     * startDriversListener()
     * Listed on all the driver flows and forward any events to the publishMetricsFlow
     */
    override suspend fun startDriversListener() = coroutineScope{
        val job = withContext(Dispatchers.IO) {
            merge(*driverFlows.toTypedArray())
                .toMetricNameValues()
                .collect { lm -> publishMetricsFlow.emit(lm) }
        }
    }

    /**
     * toMetricNameValues()
     * Extension function to Convert a flow of
     * List<DriverFunctionMetricValue> to a flow of List<MetricNameValue>
     */
    private fun Flow<List<DriverFunctionMetricValue>>.toMetricNameValues(): Flow<List<MetricNameValue>> =
        transform { ml ->
            emit(
                ml.map { m ->
                    MetricNameValue(
                        metricName = checkNotNull(
                            driverFunctionsToMetrics[m.driver]?.get(m.function),
                            lazyMessage = { "Metric name for driver ${m.driver} function ${m.function} not found" }
                        ),
                        value = m.value
                    )
                })
        }

    /**
     * startUpdates()
     *
     * Iterates through all drivers and starts the asyncRead (if any)
     */
    override suspend fun startUpdates() {
        logger.info { "Starting updates..." }
        drivers.values.forEach { d -> d.startUpdates() }
        logger.info { "Updates started" }
    }

    /**
     *  stopUpdates()
     *
     * Iterates through all drivers and stops updates (if any)
     */
    override suspend fun stopUpdates() {
        logger.info { "Stopping updates..." }
        drivers.values.forEach { d -> d.stopUpdates() }
    }

    override suspend fun writeMetric(metric: String, value: MetricValue) {
        // Find the driver name and function
        val driverFunction = checkNotNull(
            metricsTodriverFunctions[metric],
            lazyMessage = { "No driver found for metric $metric" }
        )
        val driver = checkNotNull(
            drivers[driverFunction.first],
            lazyMessage = { "No driver instance for metric ${driverFunction.first}" }
        )
        driver.writeMetric(driverFunction.second, value)
    }

    /**
     * readAllMetrics()
     *
     * Used in the creation of the NBIRTH. Calls
     * ewadAllMetrics() on all drivers in PARALLEL
     * then flattens and transforms to a list of
     * MetricNameValues
     *
     * @return list of metric name values
     */
    override suspend fun readAllMetrics(): List<MetricNameValue> = coroutineScope {
        //drivers.keys.forEach { d -> logger.info { "DRIVER  $d" } }
        val allReads = drivers.values.map { d -> async { d.readAllMetrics() } }
        val allMetrics = awaitAll(*allReads.toTypedArray())
            .flatten()
            .map { m -> MetricNameValue(
                metricName = checkNotNull(
                    driverFunctionsToMetrics[m.driver]?.get(m.function),
                    lazyMessage = { "Metric name for driver ${m.driver} function ${m.function} not found" }
                ),
                value = m.value
            )}

        return@coroutineScope allMetrics
    }
}


