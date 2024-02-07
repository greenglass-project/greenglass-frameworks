package io.greenglass.node.core.services

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CConfig
import com.pi4j.io.i2c.I2CProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.TimeUnit


class GpioService {

    /**
     * DigitalInputController
     *
     * @property activeHigh
     * @constructor
     * TODO
     *
     * @param pin
     * @param pull
     * @param debounce
     */
    inner class DigitalInputController(pin : Int,
                                       pull : PullResistance,
                                       debounce : Long,
                                       private val activeHigh : Boolean
    ) {

        private val input : DigitalInput
        init {
            val config: DigitalInputConfig = DigitalInput
                .newConfigBuilder(pi4j)
                .address(pin)
                .pull(pull)
                .debounce(debounce, TimeUnit.MILLISECONDS)
                .build()

            input = pi4j.create(config)
        }

        fun read() = booleanState(activeHigh, input.isHigh)
        fun subscribe() : SharedFlow<Boolean> {
            val flow = MutableSharedFlow<Boolean>()
            input.addListener(object : DigitalStateChangeListener {
                override fun onDigitalStateChange(event: DigitalStateChangeEvent<out Digital<*, *, *>>) {
                    flow.tryEmit(booleanState(activeHigh,event.state() == DigitalState.HIGH))
                }
            })
            return flow.asSharedFlow()
        }
        fun unSubscribe() {
            input.removeListener() // to do
        }
    }

    /**
     * DigitalOutputController
     *
     * @property activeHigh
     * @constructor
     * TODO
     *
     * @param pin
     * @param initialState
     * @param shutdownState
     */
    inner class DigitalOutputController(pin : Int,
                                        initialState : DigitalState,
                                        shutdownState : DigitalState,
                                        private val activeHigh : Boolean
    ) {
        private val output : DigitalOutput

        init {
            val config : DigitalOutputConfig = DigitalOutput
                .newConfigBuilder(pi4j)
                .address(pin)
                .shutdown(digitalState(activeHigh, initialState))
                .initial(digitalState(activeHigh, shutdownState))
                //.provider("pigpio-digital-output")
                .build()

            output = pi4j.create(config)
        }

        fun read() : Boolean = booleanState(activeHigh, output.isHigh)
        fun write(state : Boolean) { output.setState(booleanState(activeHigh, state)) }
    }

    /**
     * TODO
     *
     * @constructor
     * TODO
     *
     * @param bus
     * @param device
     */
    inner class I2cController(bus : Int, device : Int) {

        private val dev : I2C
        init {

            val i2CProvider : I2CProvider = pi4j.provider("linuxfs-i2c");

            val i2cConfig: I2CConfig = I2C
                .newConfigBuilder(pi4j)
                .bus(bus)
                .device(device)
                .build()

            dev = i2CProvider.create(i2cConfig)
        }

        fun writeBytes(bytes : ByteArray) = dev.write(bytes)
        fun readBytes(length : Int) = dev.readNBytes(length)
    }

    var pi4j : Context = Pi4J.newAutoContext();

    fun digitalInput(pin : Int,
                     pull : PullResistance,
                     debounce : Long,
                     activeHigh : Boolean
    ) = DigitalInputController(pin, pull, debounce, activeHigh)

    fun digitalOutput(pin : Int,
                      initialState : DigitalState,
                      shutdownState : DigitalState,
                      activeHigh : Boolean
    ) = DigitalOutputController(pin, initialState, shutdownState, activeHigh)

    fun i2c(bus : Int, device : Int) = I2cController(bus,device)

    private fun booleanState(activeHigh : Boolean, state : Boolean) : Boolean {
        return when (state) {
            true -> activeHigh
            false -> !activeHigh
        }
    }

    private fun digitalState(activeHigh : Boolean, state : DigitalState) : DigitalState {
        return when (state) {
            DigitalState.HIGH -> if (activeHigh) DigitalState.HIGH else DigitalState.LOW
            else -> if (activeHigh) DigitalState.LOW else DigitalState.HIGH
        }
    }

    companion object {
        @Volatile
        private var instance: GpioService? = null

        val gpio: GpioService
            get() {
                if (instance == null) {
                    synchronized(this) {
                        if (instance == null) {
                            instance = GpioService()
                        }
                    }
                }
                return instance!!
            }
    }
}