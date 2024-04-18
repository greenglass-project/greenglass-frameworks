package io.greenglass.node.core.models

import kotlinx.serialization.Serializable

@Serializable
open class NodeSettings(val groupId : String,
                        val hostId : String,
                        val broker : String
)
