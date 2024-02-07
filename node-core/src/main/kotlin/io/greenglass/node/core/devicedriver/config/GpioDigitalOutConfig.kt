package io.greenglass.node.core.devicedriver.config

import com.pi4j.io.gpio.digital.DigitalState

class GpioDigitalOutConfig(val pin : Int,
                           val initialState : DigitalState,
                           val shutdownState : DigitalState,
                           val activeHigh : Boolean
) : DriverConfig() {
    companion object
}