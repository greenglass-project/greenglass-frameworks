package io.greenglass.node.core.devicedriver.drivers

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.devicedriver.DeviceDriver
import io.greenglass.node.core.devicedriver.DriverFunction
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.devicedriver.config.GpioDigitalInConfig
import io.greenglass.node.core.services.GpioService
import io.greenglass.sparkplug.datatypes.MetricNameValue
import io.greenglass.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class GpioDigitalInDriver(name : String, metrics : Map<String, String>, config : DriverConfig) : DeviceDriver(name, metrics, config){

    val c = checkNotNull(config as? GpioDigitalInConfig)
    val device : GpioService.DigitalInputController
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "Create DigitalInput on pin ${c.pin}"}
        device = GpioService.gpio.digitalInput(
            pin = c.pin,
            pull = c.pull,
            debounce = c.debounce,
            activeHigh = c.activeHigh
        )
    }

    inner class State(val metric : String) : DriverFunction(metric) {
        var job : Job? = null
        override suspend fun readValue(): MetricValue = MetricValue(device.read())
        override suspend fun startAsyncRead() {
            job = CoroutineScope(Dispatchers.Default).launch {
                device.subscribe().collect { s -> valueFlow.emit(MetricNameValue(metric, MetricValue(s)))}
            }
        }
        override suspend fun stopAsyncRead() {
            job?.cancel()
            device.unSubscribe()
        }
    }

    companion object {
        val type: String = "gpio_digital_in"
    }
}