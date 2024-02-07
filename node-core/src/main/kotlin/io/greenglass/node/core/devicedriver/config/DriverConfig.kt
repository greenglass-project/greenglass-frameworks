package io.greenglass.node.core.devicedriver.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value =I2cConfig::class, name = "i2c"),
    Type(value = GpioDigitalOutConfig::class, name = "gpioDigitalOut"),
    Type(value = GpioDigitalInConfig::class, name = "gpioDigitalIn")
)
open class DriverConfig