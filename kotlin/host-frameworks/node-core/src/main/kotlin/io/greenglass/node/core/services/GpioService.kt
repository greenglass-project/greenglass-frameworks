package io.greenglass.node.core.services

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.*
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CConfig
import com.pi4j.io.i2c.I2CProvider
import com.pi4j.io.serial.Serial
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class GpioService(val backgroundScope : CoroutineScope) {

    private val logger = KotlinLogging.logger {}

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
            logger.debug { "Creatring Digital Input on pin = $pin pullup = ${pull.name} debounce = $debounce active high  $activeHigh" }
            val config: DigitalInputConfig = DigitalInput
                .newConfigBuilder(pi4j)
                .address(pin)
                .pull(pull)
                .debounce(debounce, TimeUnit.MILLISECONDS)
                .provider("pigpio-digital-input")

                .build()

            input = pi4j.create(config)
        }

        fun read() = booleanState(activeHigh, input.isHigh)

        fun subscribe() : StateFlow<Boolean> {
            logger.debug { "Subscribe to digital input}" }
            val flow = MutableStateFlow(read())
            input.addListener(object : DigitalStateChangeListener {
                override fun onDigitalStateChange(event: DigitalStateChangeEvent<out Digital<*, *, *>>) {
                    logger.debug { "Input changed ${event.state().name}" }
                    val res = flow.tryEmit(booleanState(activeHigh,event.state() == DigitalState.HIGH))
                    logger.debug { "Publish change result  $res" }
                }
            })
            return flow.asStateFlow()
        }
        fun unSubscribe() {
            logger.debug { "un-subscribe to digital input}" }
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
            val config : DigitalOutputConfig =
                DigitalOutput
                    .newConfigBuilder(pi4j)
                    .address(pin)
                    .shutdown(initialDigitalState(activeHigh))
                    .initial(initialDigitalState(activeHigh))
                    .provider("pigpio-digital-output")
                    .build()

            output = pi4j.create(config)
        }

        fun read() : Boolean = booleanState(activeHigh, output.isHigh)
        fun write(state : Boolean) { output.setState(booleanState(activeHigh, state)) }
    }

    /**
     *
     * @constructor
     * TODO
     *
     * @param bus
     * @param device
     */
    inner class I2cController(val bus : Int, val device : Int) {

        private val dev: I2C

        init {

            val i2CProvider: I2CProvider = pi4j.provider("linuxfs-i2c");

            val i2cConfig: I2CConfig = I2C
                .newConfigBuilder(pi4j)
                .bus(bus)
                .device(device)
                .build()

            dev = i2CProvider.create(i2cConfig)
        }

        fun writeBytes(bytes: ByteArray) = dev.write(bytes)

        fun readBytes(length : Int) : ByteArray = dev.readNBytes(length)
    }


    inner class SerialController(baudRate: Int, dataBits: Int,stopBits: Int) {

        private val dev : Serial
        private var job : Job? = null
        private val subscriptionFlow : MutableSharedFlow<Byte> = MutableSharedFlow()

        init {
            val serialConfig = Serial.newConfigBuilder(pi4j)
                .provider("pigpio-serial")
                .device("/dev/ttyS0")
                .dataBits(dataBits)
                .baud(baudRate)
                .stopBits(stopBits)
                .build()

            dev = pi4j.create(serialConfig)
        }

        suspend fun open() = coroutineScope{
            logger.debug { "opening ${dev.name}"}
            try {
                dev.open()
                while (!dev.isOpen) {
                    logger.debug { "is open? ${dev.isOpen}" }
                    delay(250);
                }
                subscribe()
            } catch (ex:Exception) {
                logger.error { ex.stackTraceToString() }
            }
        }

        @OptIn(FlowPreview::class)
        suspend fun read(count : Int) : ByteArray = coroutineScope{
            return@coroutineScope subscriptionFlow
                .asSharedFlow()
                .take(count)
                .timeout(4000.milliseconds)
                .catch { e -> emit(0) }
                .toList()
                .toByteArray()
        }

        private fun subscribe() {
            if(job == null) {
                job = backgroundScope.launch {
                    while(true) {
                        val available = dev.available()
                        repeat(available) {
                            subscriptionFlow.emit(dev.read().toByte())
                        }
                    }
                }
            }
            //return subscriptionFlow.asSharedFlow()
        }

        fun unSubscribe() {
            job?.cancel()
            job = null
        }

        fun send(data : IntArray) {
            logger.debug { "sending ${data.joinToString( separator = ",", transform = { i -> i.toString()})}" }
            val bytes = data.foldIndexed(ByteArray(data.size)) { i, a, v -> a.apply { set(i, v.toByte()) } }
            logger.debug { "sending ${bytes.joinToString( separator = ",", transform = { b -> b.toString()})}" }

            dev.write(bytes)
        }
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

    fun serial(baudRate: Int, dataBits: Int,stopBits: Int) =
        SerialController(baudRate, dataBits, stopBits)

    fun i2c(bus : Int, device : Int) = I2cController(bus,device)

    private fun booleanState(activeHigh : Boolean, state : Boolean) : Boolean {
        val newState = when (state) {
            true -> activeHigh
            false -> !activeHigh
        }
        logger.debug { "BOOLEAN STATE : activeHigh=$activeHigh : state=$state NEW STATE = $newState"}
        return newState
    }

    private fun digitalState(activeHigh : Boolean, state : DigitalState) : DigitalState {
        return when (state) {
            DigitalState.HIGH -> if (activeHigh) DigitalState.HIGH else DigitalState.LOW
            else -> if (activeHigh) DigitalState.LOW else DigitalState.HIGH
        }
    }

    private fun initialDigitalState(activeHigh : Boolean) =
        if(activeHigh) DigitalState.LOW else DigitalState.HIGH
}