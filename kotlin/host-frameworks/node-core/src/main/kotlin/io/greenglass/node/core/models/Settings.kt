package io.greenglass.node.core.models

import kotlinx.serialization.Serializable

@Serializable
class Settings(val nodeType : String,
               val nodeId : String,
               val groupId : String,
               val hostId : String,
               val broker : String
) {
    fun isDefined() = nodeType.isNotEmpty() &&
                nodeId.isNotEmpty() &&
                groupId.isNotEmpty() &&
                hostId.isNotEmpty() &&
                broker.isNotEmpty()
}
