package io.greenglass.node.sparkplug.datatypes

import kotlinx.serialization.Serializable
import org.eclipse.tahu.message.model.MetricDataType

enum class MetricDirection {
    read,
    write
}

@Serializable
class MetricDefinition(val metricName : String,
                       val type : MetricDataType,
                       val direction : MetricDirection,
                       val description : String,
                       val driver : String,
                       val function : String
    )
