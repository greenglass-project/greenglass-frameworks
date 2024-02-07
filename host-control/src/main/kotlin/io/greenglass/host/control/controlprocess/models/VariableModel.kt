package io.greenglass.host.control.controlprocess.models

import org.eclipse.tahu.message.model.MetricDataType

open class VariableModel(
    val variableId : String,
    val name : String,
    val description : String,
    val type : MetricDataType,
) {
    companion object
}
