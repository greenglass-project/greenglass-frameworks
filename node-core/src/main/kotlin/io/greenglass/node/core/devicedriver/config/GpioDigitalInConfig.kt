package io.greenglass.node.core.devicedriver.config

import com.pi4j.io.gpio.digital.PullResistance

class GpioDigitalInConfig(val pin : Int,
                          val pull : PullResistance,
                          val debounce : Long,
                          val activeHigh : Boolean
) : DriverConfig() {  companion object }
