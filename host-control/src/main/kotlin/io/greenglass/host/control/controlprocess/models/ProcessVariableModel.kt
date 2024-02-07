package io.greenglass.host.control.controlprocess.models

import org.eclipse.tahu.message.model.MetricDataType

open class ProcessVariableModel(variableId : String,
                                name : String,
                                description : String,
                                type : MetricDataType,
                                val minValue : Double,
                                val maxValue : Double,
                                val default : Double,
                                val tolerance : Double,
                                val units : String,
                                val decimalPlaces : Int
) : VariableModel(variableId, name, description, type) {
    companion object
}

