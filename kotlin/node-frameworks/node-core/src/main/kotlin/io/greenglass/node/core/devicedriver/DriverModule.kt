/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.devicedriver

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.collections.HashMap

import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.services.GpioService
import io.greenglass.node.core.services.PersistenceService
import io.greenglass.node.core.services.WebService
import io.greenglass.node.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.CoroutineScope
abstract class DriverModule(val name : String,
                            val config : List<DriverConfig>,
                            val gpio : GpioService,
                            val persistence : PersistenceService,
                            val webService : WebService,
                            val backgroundScope : CoroutineScope) {

    protected val writersMap : HashMap<String, WriteMetricFunction> = hashMapOf()

    abstract suspend fun initialise()

    fun registerWriteFunction(functionName : String, functionImpl : WriteMetricFunction) {
        writersMap[functionName] = functionImpl
    }

    open suspend fun registerSettingsHandler() {}

    suspend fun writeMetric(function : String, value : MetricValue) {
        val func = checkNotNull(
            writersMap[function],
            lazyMessage = { "Unknown function $function " }
        )
        func.write(value)
    }

    val valueFlow = MutableSharedFlow<List<DriverFunctionMetricValue>>(1)

    abstract suspend fun readAllMetrics() : List<DriverFunctionMetricValue>
    open fun startUpdates() {}
    open fun stopUpdates() {}
}