package io.greenglass.node.core.devicedriver.config

import com.pi4j.io.gpio.digital.DigitalState
import com.pi4j.io.gpio.digital.PullResistance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable sealed class DriverConfig() {
    @Serializable
    @SerialName("i2c")
    class I2cConfig(val device: Int, val address: Int) : DriverConfig()
    @Serializable
    @SerialName("gpioDigitalIn")
    class GpioDigitalInConfig(val pin : Int,
                              val pull : PullResistance,
                              val debounce : Long,
                              val activeHigh : Boolean) : DriverConfig()
    @Serializable
    @SerialName("gpioDigitalOut")
    class GpioDigitalOutConfig(val pin : Int,
                               val initialState : DigitalState,
                               val shutdownState : DigitalState,
                               val activeHigh : Boolean) : DriverConfig()

    @Serializable
    @SerialName("serial")
    class SerialConfig(val baudRate: Int,
                       val dataBits: Int,
                       val stopBits: Int)  : DriverConfig()
}
