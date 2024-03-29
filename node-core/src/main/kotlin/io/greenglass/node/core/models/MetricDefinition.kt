package io.greenglass.node.core.models

import io.greenglass.sparkplug.models.Metric
import io.greenglass.sparkplug.models.MetricDirection
import org.eclipse.tahu.message.model.MetricDataType

class MetricDefinition(metricName : String,
                       type : MetricDataType,
                       direction : MetricDirection,
                       description : String,
                       val driver : String,
                       val function : String
    ) : Metric(metricName, type, direction, description ){
}