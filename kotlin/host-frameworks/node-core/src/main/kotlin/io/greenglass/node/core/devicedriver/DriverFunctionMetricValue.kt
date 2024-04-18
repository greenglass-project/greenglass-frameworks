package io.greenglass.node.core.devicedriver
import io.greenglass.sparkplug.datatypes.MetricValue

class DriverFunctionMetricValue(val driver : String, val function : String, val value : MetricValue)