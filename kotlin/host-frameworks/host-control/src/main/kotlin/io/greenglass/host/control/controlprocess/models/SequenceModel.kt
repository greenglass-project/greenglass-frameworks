package io.greenglass.host.control.controlprocess.models

open class SequenceModel (
    var sequenceId : String,
    var name : String,
    var description : String,
    val sequenceVariables : List<VariableModel>?,
    val manipulatedVariables : List<VariableModel>?
) {
    companion object
}