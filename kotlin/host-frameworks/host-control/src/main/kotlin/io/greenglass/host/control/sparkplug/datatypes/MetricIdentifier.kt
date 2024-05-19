/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.sparkplug.datatypes

import org.eclipse.tahu.message.model.MetricDataType

class MetricIdentifier(val nodeName : String,
                       val metricName : String,
                       val type : MetricDataType,
    ) {
    companion object
}