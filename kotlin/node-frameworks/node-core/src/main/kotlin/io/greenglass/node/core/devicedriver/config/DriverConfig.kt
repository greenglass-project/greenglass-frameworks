package io.greenglass.node.core.devicedriver.config

import com.pi4j.io.gpio.digital.DigitalState
import com.pi4j.io.gpio.digital.PullResistance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable sealed class DriverConfig() {
    abstract val name: String
}
    @Serializable
    @SerialName("i2c")
    class I2cConfig(override val name: String, val device: Int, val address: Int) : DriverConfig()

    @Serializable
    @SerialName("gpioDigitalIn")
    class GpioDigitalInConfig(override val name: String,
                              val pin : Int,
                              val pull : PullResistance,
                              val debounce : Long,
                              val activeHigh : Boolean) : DriverConfig()
    @Serializable
    @SerialName("gpioDigitalOut")
    class GpioDigitalOutConfig(override val name: String,
                               val pin : Int,
                               val initialState : DigitalState,
                               val shutdownState : DigitalState,
                               val activeHigh : Boolean) : DriverConfig()

    @Serializable
    @SerialName("serial")
    class SerialConfig(override val name: String,
                       val baudRate: Int,
                       val dataBits: Int,
                       val stopBits: Int)  : DriverConfig()

