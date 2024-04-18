package io.greenglass.host.control.controlprocess.models

import io.greenglass.sparkplug.datatypes.MetricValue

class VariableValueEventModel(
    val instanceId : String,
    val processId : String,
    val variableId : String,
    val value : MetricValue
)
