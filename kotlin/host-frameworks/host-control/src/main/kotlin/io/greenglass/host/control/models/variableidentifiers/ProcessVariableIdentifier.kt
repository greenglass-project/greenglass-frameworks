package io.greenglass.host.control.models.variableidentifiers

class ProcessVariableIdentifier(val processId : String,
                                val variableId : String
) : VariableIdentifier {
    override val variableKey = "$processId/$variableId"
    companion object
}
