package io.greenglass.host.control.sparkplug.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.eclipse.tahu.message.model.MetricDataType

@JsonIgnoreProperties(ignoreUnknown = true)
open class Metric(
    val metricName : String,
    val type : MetricDataType,
    val direction : MetricDirection,
    val description : String
) {
    companion object
}
