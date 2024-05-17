package io.greenglass.host.control.sparkplug.datatypes

class NodeMetricValue(val modeName : String,
                      val metricName : String,
                      val value : MetricValue
) {
    constructor(modeName : String, metricName : String) : this(modeName, metricName, MetricValue())
}
