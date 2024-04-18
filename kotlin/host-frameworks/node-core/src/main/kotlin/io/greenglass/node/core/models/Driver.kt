package io.greenglass.node.core.models

import io.greenglass.node.core.devicedriver.config.DriverConfig

class Driver(
    val name : String,
    val type: String,
    val config : List<DriverConfig>,
)

