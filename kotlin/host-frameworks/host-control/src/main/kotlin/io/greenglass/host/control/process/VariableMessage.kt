package io.greenglass.host.control.process

import io.greenglass.host.control.datatypes.VariableValue

class VariableMessage(val processId : String, val variableId : String, val value : VariableValue?)

