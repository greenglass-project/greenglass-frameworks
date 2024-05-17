package io.greenglass.host.control.models.variableidentifiers

class MetricVariableIdentifier(val nodeName : String,
                               val metricName : String
) : VariableIdentifier {
    override val variableKey = "$nodeName/$metricName"
    companion object
}