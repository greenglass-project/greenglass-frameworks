/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.devicedriver.drivers

import io.klogging.NoCoLogging
import kotlinx.coroutines.CoroutineScope

import io.greenglass.node.core.devicedriver.DriverModule
import io.greenglass.node.core.devicedriver.WriteMetricFunction
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.services.GpioService
import io.greenglass.node.core.services.PersistenceService
import io.greenglass.node.core.services.WebService
import io.greenglass.node.sparkplug.datatypes.MetricValue

class NullDriver(name : String,
                      config : List<DriverConfig>,
                      gpio : GpioService,
                      persistence : PersistenceService,
                      webService : WebService,
                      backgroundScope : CoroutineScope
) : DriverModule(name, config, gpio, persistence, webService, backgroundScope), NoCoLogging {

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