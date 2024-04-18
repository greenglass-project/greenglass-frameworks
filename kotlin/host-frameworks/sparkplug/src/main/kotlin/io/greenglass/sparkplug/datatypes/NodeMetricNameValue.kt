package io.greenglass.sparkplug.datatypes

class NodeMetricNameValue(val nodeId : String,
                          metricName : String,
                          value : MetricValue
) : MetricNameValue(metricName, value) {

    constructor(nodeId : String, metricName : String) : this(nodeId, metricName, MetricValue())
}
