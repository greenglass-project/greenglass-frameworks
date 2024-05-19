/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.datatypes

import kotlinx.coroutines.flow.StateFlow

class IdentifiedFlow(val processId : String,
                     val variableId : String,
                     val flow : StateFlow<VariableValue>
)