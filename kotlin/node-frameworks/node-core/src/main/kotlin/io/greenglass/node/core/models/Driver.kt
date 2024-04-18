package io.greenglass.node.core.models

import io.greenglass.node.core.devicedriver.config.DriverConfig
import kotlinx.serialization.Serializable

@Serializable
class Driver(
    val name : String,
    val type: String,
    val config : List<DriverConfig>,
)

