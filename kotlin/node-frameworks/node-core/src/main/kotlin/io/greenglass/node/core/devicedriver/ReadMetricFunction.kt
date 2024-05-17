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


abstract class ReadMetricFunction(driverModule : io.greenglass.node.core.devicedriver.DriverModule, functionName : String) : io.greenglass.node.core.devicedriver.DriverFunction(driverModule, functionName) {
    fun driverFunctionMetricValue(value : MetricValue) = DriverFunctionMetricValue(driver.name, functionName, value)
}