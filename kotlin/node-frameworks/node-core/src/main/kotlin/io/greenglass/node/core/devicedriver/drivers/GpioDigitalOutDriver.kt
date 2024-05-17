/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.devicedriver.drivers

import io.greenglass.node.core.devicedriver.*

import io.klogging.NoCoLogging
import kotlinx.coroutines.CoroutineScope

import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.devicedriver.config.GpioDigitalOutConfig
import io.greenglass.node.core.services.BoolValue
import io.greenglass.node.core.services.GpioService
import io.greenglass.node.core.services.PersistenceService
import io.greenglass.node.core.services.WebService
import io.greenglass.node.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class GpioDigitalOutDriver(name : String,
                                config : List<DriverConfig>,
                                gpio : GpioService,
                                persistence : PersistenceService,
                                webService : WebService,
                                backgroundScope : CoroutineScope
)  : NoCoLogging, DriverModule(name, config, gpio, persistence, webService, backgroundScope) {

    private val c = checkNotNull(config.firstOrNull() as? GpioDigitalOutConfig)
    private val device : GpioService.DigitalOutputController

    private val outputState = MutableStateFlow(false)

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
                    val state = value.boolean
                    logger.debug { "WRITING $state to pin ${c.pin}" }
                    device.write(state)
                    valueFlow.emit(listOf(driverFunctionMetricValue(value)))
                    outputState.emit(state)
                }
            }

            override suspend fun read() = MetricValue(device.read())
        }

        val stateEventFlow = outputState
            .asSharedFlow()
            .transform { e -> emit(Json.encodeToString(BoolValue(e))) }
            .stateIn(backgroundScope)
        webService.addEventPublisher("/driver/$name/state", stateEventFlow)
    }
    override suspend fun readAllMetrics() :  List<DriverFunctionMetricValue> {
        val s = state.read()
        outputState.emit(s.boolean)
        return listOf(state.driverFunctionMetricValue(s))
    }
    companion object {
        val type: String = "gpio_digital_out"
    }
}