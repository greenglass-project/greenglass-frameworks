/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.sparkplug.datatypes

class NodeMetricValue(val modeName : String,
                      val metricName : String,
                      val value : MetricValue
) {
    constructor(modeName : String, metricName : String) : this(modeName, metricName, MetricValue())
}
