package io.greenglass.node.core.services.sparkplugservice

import kotlinx.serialization.Serializable

@Serializable
data class HostState(val online: Boolean, val timestamp: Long)
