package io.greenglass.node.core.devicedriver

import io.greenglass.node.sparkplug.datatypes.MetricValue


abstract class ReadMetricFunction(driverModule : io.greenglass.node.core.devicedriver.DriverModule, functionName : String) : io.greenglass.node.core.devicedriver.DriverFunction(driverModule, functionName) {
    fun driverFunctionMetricValue(value : MetricValue) = DriverFunctionMetricValue(driver.name, functionName, value)
}