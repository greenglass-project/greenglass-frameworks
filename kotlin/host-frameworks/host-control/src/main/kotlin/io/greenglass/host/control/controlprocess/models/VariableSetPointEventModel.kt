package io.greenglass.host.control.controlprocess.models

import io.greenglass.sparkplug.datatypes.MetricValue

class VariableSetPointEventModel(
    val instanceId : String,
    val processId : String,
    val variableId : String,
    val setpoint : MetricValue
)
