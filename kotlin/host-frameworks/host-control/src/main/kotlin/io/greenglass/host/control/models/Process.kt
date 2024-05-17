/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.models

open class Process(val processId : String,
                    val name : String,
                    val description : String,
                    val inputVariables : List<Variable>,
                    val outputVariables : List<Variable>
) {
    companion object
}
