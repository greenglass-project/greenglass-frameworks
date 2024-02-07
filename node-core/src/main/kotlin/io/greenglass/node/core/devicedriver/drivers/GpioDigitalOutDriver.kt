package io.greenglass.node.core.devicedriver.drivers

import io.github.oshai.kotlinlogging.KotlinLogging

import io.greenglass.node.core.devicedriver.DeviceDriver
import io.greenglass.node.core.devicedriver.DriverFunction
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.devicedriver.config.GpioDigitalOutConfig
import io.greenglass.node.core.services.GpioService
import io.greenglass.node.core.services.GpioService.Companion.gpio

import io.greenglass.sparkplug.datatypes.MetricNameValue
import io.greenglass.sparkplug.datatypes.MetricValue

open class GpioDigitalOutDriver(name : String, metrics : Map<String, String>, config : DriverConfig) : DeviceDriver(name, metrics, config){

    val c = checkNotNull(config as? GpioDigitalOutConfig)
    val device : GpioService.DigitalOutputController
    private val logger = KotlinLogging.logger {}

    init {
        logger.debug { "Create DigitalOutput on pin ${c.pin}"}
        device = gpio.digitalOutput(
            pin = c.pin,
            initialState = c.initialState,
            shutdownState = c.shutdownState,
            activeHigh = c.activeHigh)
    }

    inner class State(val metric : String) : DriverFunction(metric) {
        override suspend fun readValue(): MetricValue = MetricValue(device.read())
        override suspend fun writeValue(value: MetricValue) {
            if(value.isBoolean) {
                device.write(value.boolean)
                valueFlow.tryEmit(MetricNameValue(metric, value))
            }
        }
    }
    companion object {
        val type: String = "gpio_digital_out"
    }
}