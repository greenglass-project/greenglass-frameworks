/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
fun envVariableOrDefault(envVariable : String, default : String) = System.getenv(envVariable) ?: default
fun envVariableOrDefault(envVariable : String, default : Int) = System.getenv(envVariable)?.toInt() ?: default
