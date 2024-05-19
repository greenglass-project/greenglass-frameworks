/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.process

import io.greenglass.host.control.datatypes.VariableValue

class VariableMessage(val processId : String, val variableId : String, val value : VariableValue?)

