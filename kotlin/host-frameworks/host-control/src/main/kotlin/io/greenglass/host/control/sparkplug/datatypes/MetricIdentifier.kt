package io.greenglass.host.control.sparkplug.datatypes

import org.eclipse.tahu.message.model.MetricDataType

class MetricIdentifier(val nodeName : String,
                       val metricName : String,
                       val type : MetricDataType,
    ) {
    companion object
}