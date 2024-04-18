package io.greenglass.node.core.devicedriver.drivers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.devicedriver.DriverModule
import io.greenglass.node.core.devicedriver.WriteMetricFunction
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.services.GpioService
import io.greenglass.node.core.services.PersistenceService
import io.greenglass.node.core.services.WebService
import io.greenglass.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.CoroutineScope

open class NullDriver(name : String,
                      config : List<DriverConfig>,
                      gpio : GpioService,
                      persistence : PersistenceService,
                      webService : WebService,
                      backgroundScope : CoroutineScope
) : DriverModule(name, config, gpio, persistence, webService, backgroundScope) {

    private val logger = KotlinLogging.logger {}
    private lateinit var state : WriteMetricFunction

    init {
        logger.debug { "Create NullDriver"}
    }

    override suspend fun initialise() {
        state = object : WriteMetricFunction(this, "State") {
            init {
                driver.registerWriteFunction(functionName, this)
            }

            override suspend fun write(value: MetricValue) {
                logger.debug { "DIGITAL-OUT WRITE VALUE ${value.value}" }
                if (value.isBoolean) {
                    valueFlow.emit(listOf(driverFunctionMetricValue(value)))
                }
            }

            override suspend fun read() = MetricValue(true)
        }
    }

    override suspend fun readAllMetrics() =  listOf(state.driverFunctionMetricValue(state.read()))


    companion object {
        val type: String = "null_driver"
    }
}