/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.devicedriver

import io.greenglass.node.sparkplug.datatypes.MetricValue

class DriverFunctionMetricValue(val driver : String, val function : String, val value : MetricValue)