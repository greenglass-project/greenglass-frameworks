package io.greenglass.node.core.devicedriver.drivers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.devicedriver.*

import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.services.GpioService
import io.greenglass.node.core.services.PersistenceService
import io.greenglass.node.core.services.WebService
import io.greenglass.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.CoroutineScope

open class GpioDigitalOutDriver(name : String,
                                config : List<DriverConfig>,
                                gpio : GpioService,
                                persistence : PersistenceService,
                                webService : WebService,
                                backgroundScope : CoroutineScope
)  : DriverModule(name, config, gpio, persistence, webService, backgroundScope) {

    private val c = checkNotNull(config.firstOrNull() as? DriverConfig.GpioDigitalOutConfig)
    private val device : GpioService.DigitalOutputController
    private val logger = KotlinLogging.logger {}

    private lateinit var state : WriteMetricFunction

    init {
        logger.debug { "Create DigitalOutput on pin ${c.pin}"}
        device = gpio.digitalOutput(
            pin = c.pin,
            initialState = c.initialState,
            shutdownState = c.shutdownState,
            activeHigh = c.activeHigh)
    }

    override suspend fun initialise() {
        state = object : WriteMetricFunction(this, "State") {
            init {
                driver.registerWriteFunction(functionName, this)
            }

            override suspend fun write(value: MetricValue) {
                logger.debug { "DIGITAL-OUT WRITE VALUE ${value.value}" }
                if (value.isBoolean) {
                    logger.debug { "WRITING to pin ${c.pin}" }
                    device.write(value.boolean)
                    valueFlow.emit(listOf(driverFunctionMetricValue(value)))
                }
            }

            override suspend fun read() = MetricValue(device.read())
        }
    }
    override suspend fun readAllMetrics() = listOf(state.driverFunctionMetricValue(state.read()))
    companion object {
        val type: String = "gpio_digital_out"
    }
}