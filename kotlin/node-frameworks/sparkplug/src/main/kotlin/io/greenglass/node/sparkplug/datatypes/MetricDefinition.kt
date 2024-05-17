package io.greenglass.node.sparkplug.datatypes

import kotlinx.serialization.Serializable
import org.eclipse.tahu.message.model.MetricDataType

@Serializable
open class MetricDefinition(
    val metricName : String,
    val type : MetricDataType,
    val direction : MetricDirection,
    val description : String,
    val driver: String,
    val function: String
)