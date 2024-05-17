/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.models

import io.greenglass.host.control.datatypes.VariableValueType
import io.greenglass.host.control.models.variableidentifiers.MetricVariableIdentifier
import io.greenglass.host.control.models.variableidentifiers.ProcessVariableIdentifier
import io.greenglass.host.control.sparkplug.datatypes.MetricIdentifier

open class Variable(
    val variableId : String,
    val name : String,
    val description : String,
    val type : VariableValueType,
    val sharedFlow : ProcessVariableIdentifier?,
    val metricFlow : MetricVariableIdentifier?,
    val valueProvider : String?,
) {
    companion object
}
