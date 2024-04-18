package io.greenglass.node.core.devicedriver

import io.greenglass.node.sparkplug.datatypes.MetricValue

class DriverFunctionMetricValue(val driver : String, val function : String, val value : MetricValue)