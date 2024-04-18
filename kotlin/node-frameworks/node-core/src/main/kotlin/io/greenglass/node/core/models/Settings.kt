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