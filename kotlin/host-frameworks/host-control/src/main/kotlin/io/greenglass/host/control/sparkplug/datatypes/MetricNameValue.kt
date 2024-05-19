/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.sparkplug.datatypes

open class MetricNameValue(
    val metricName : String,
    val value : MetricValue
) {
    fun toMetric() = value.toMetric(metricName)
}