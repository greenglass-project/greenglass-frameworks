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


abstract class WriteMetricFunction(driver : DriverModule, functionName : String) : DriverFunction(driver, functionName){
    abstract suspend fun write(value : MetricValue)
    abstract suspend fun read() : MetricValue
    fun driverFunctionMetricValue(value : MetricValue) = DriverFunctionMetricValue(driver.name, functionName, value)
}