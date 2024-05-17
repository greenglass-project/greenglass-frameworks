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
class Settings(val nodeType : String,
               val nodeId : String,
               val groupId : String,
               val hostId : String,
               val broker : String
) {
    constructor(nodeType : String, 
                nodeId : String, 
                nodeSettings : NodeSettings
    ) : this(nodeType,
        nodeId,
        nodeSettings.groupId,
        nodeSettings.hostId,
        nodeSettings.broker
    )
}