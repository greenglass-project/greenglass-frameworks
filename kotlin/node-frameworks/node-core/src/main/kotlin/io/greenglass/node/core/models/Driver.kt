/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.models

import io.greenglass.node.core.devicedriver.config.DriverConfig
import kotlinx.serialization.Serializable

@Serializable
class Driver(
    val name : String,
    val type: String,
    val config : List<DriverConfig>,
)

