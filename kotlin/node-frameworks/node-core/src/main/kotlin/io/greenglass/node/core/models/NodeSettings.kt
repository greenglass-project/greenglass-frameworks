/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.models

import kotlinx.serialization.Serializable

@Serializable
open class NodeSettings(val groupId : String,
                        val hostId : String,
                        val broker : String
)
