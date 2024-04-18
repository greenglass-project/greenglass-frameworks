package io.greenglass.node.core.devicedriver

import io.greenglass.sparkplug.datatypes.MetricValue

abstract class ReadMetricFunction(driverModule : DriverModule, functionName : String) : DriverFunction(driverModule, functionName) {
    fun driverFunctionMetricValue(value : MetricValue) = DriverFunctionMetricValue(driver.name, functionName, value)
}