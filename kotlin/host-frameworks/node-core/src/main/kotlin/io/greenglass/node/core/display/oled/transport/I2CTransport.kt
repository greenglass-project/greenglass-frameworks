package io.greenglass.node.core.display.oled.transport

import com.pi4j.Pi4J
import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import com.pi4j.io.i2c.I2C
import com.pi4j.io.i2c.I2CProvider
import com.pi4j.library.pigpio.PiGpio
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider
import com.pi4j.plugin.pigpio.provider.i2c.PiGpioI2CProvider

/**
 * A [Transport] implementation that utilises I<sup>2</sup>C.
 *
 * @author fauxpark
 */
class I2CTransport(rstPin: Int, bus: Int, address: Int) : Transport {
    /**
     * The internal Pi4J context.
     */
    private val context: Context

    /**
     * The GPIO pin corresponding to the RST line on the display.
     */
    private val rstPin: DigitalOutput

    /**
     * The internal I<sup>2</sup>C device.
     */
    private val i2c: I2C

    /**
     * I2CTransport constructor.
     *
     * @param rstPin The GPIO pin to use for the RST line.
     * @param bus The I<sup>2</sup>C bus to use.
     * @param address The I<sup>2</sup>C address of the display.
     */
    init {
        val gpio = PiGpio.newNativeInstance()
        context = Pi4J.newContextBuilder()
            .noAutoDetectProviders()
            .add(
                PiGpioI2CProvider.newInstance(gpio),
                PiGpioDigitalOutputProvider.newInstance(gpio)
            )
            .build()
        val rstPinConfig = DigitalOutput.newConfigBuilder(context)
            .address(rstPin)
            .build()
        this.rstPin = context.dout<DigitalOutputProvider>().create(rstPinConfig)
        val i2cConfig = I2C.newConfigBuilder(context)
            .bus(bus)
            .device(address)
            .build()
        i2c = context.i2c<I2CProvider>().create(i2cConfig)
    }

    override fun reset() {
        try {
            rstPin.high()
            Thread.sleep(1)
            rstPin.low()
            Thread.sleep(10)
            rstPin.high()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun shutdown() {
        context.shutdown()
    }

    override fun command(command: Int, vararg params: Int) {
        val commandBytes = ByteArray(params.size + 2)
        commandBytes[0] = (0 shl DC_BIT).toByte()
        commandBytes[1] = command.toByte()
        for (i in params.indices) {
            commandBytes[i + 2] = params[i].toByte()
        }
        i2c.write(*commandBytes)
    }

    override fun data(data: ByteArray) {
        val dataBytes = ByteArray(data.size + 1)
        dataBytes[0] = (1 shl DC_BIT).toByte()
        System.arraycopy(data, 0, dataBytes, 1, data.size)
        i2c.write(*dataBytes)
    }

    companion object {
        /**
         * The Data/Command bit position.
         */
        private const val DC_BIT = 6
    }
}
