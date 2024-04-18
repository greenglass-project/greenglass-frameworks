package io.greenglass.host.control.controlprocess.models

open class ProcessModel (
    val process : String,
    val processId : String,
    val name : String,
    val description : String,
    val processVariables : List<ProcessVariableModel>?,
    val manipulatedVariables : List<VariableModel>?
) {
    companion object
}