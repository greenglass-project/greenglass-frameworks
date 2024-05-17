/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.sparkplug

import io.greenglass.node.sparkplug.datatypes.MetricNameValue
import io.greenglass.node.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.flow.SharedFlow

interface Drivers {
    fun subscribe(): SharedFlow<List<MetricNameValue>>
    suspend fun startDriversListener()
    suspend fun startUpdates()
    suspend fun stopUpdates()
    suspend fun writeMetric(metric: String, value: MetricValue)
    suspend fun readAllMetrics(): List<MetricNameValue>
}


