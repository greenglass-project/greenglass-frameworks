package io.greenglass.node.core.devicedriver

import io.greenglass.sparkplug.datatypes.MetricValue

abstract class DriverFunction(val metricName : String) {
    abstract suspend fun readValue() : MetricValue
    open suspend fun writeValue(value: MetricValue) = Unit
    open suspend fun startAsyncRead() = Unit
    open suspend fun stopAsyncRead() = Unit
}