package io.greenglass.sparkplug.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable
import org.eclipse.tahu.message.model.MetricDataType

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
open class Metric(
    val metricName : String,
    val type : MetricDataType,
    val direction : MetricDirection,
    val description : String
) {
    companion object
}
