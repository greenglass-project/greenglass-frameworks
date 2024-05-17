package io.greenglass.host.control.datatypes

import kotlinx.coroutines.flow.StateFlow

class IdentifiedFlow(val processId : String,
                     val variableId : String,
                     val flow : StateFlow<VariableValue>
)