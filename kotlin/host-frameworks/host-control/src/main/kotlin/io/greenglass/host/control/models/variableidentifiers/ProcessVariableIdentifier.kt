/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.models.variableidentifiers

class ProcessVariableIdentifier(val processId : String,
                                val variableId : String
) : VariableIdentifier {
    override val variableKey = "$processId/$variableId"
    companion object
}
