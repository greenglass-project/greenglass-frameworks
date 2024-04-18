package io.greenglass.node.core.display.oled.transport

/**
 * An interface for defining transports.
 *
 * @author fauxpark
 */
interface Transport {
    /**
     * Reset the display.
     */
    fun reset()

    /**
     * Shutdown the display.
     */
    fun shutdown()

    /**
     * Send a command to the display.
     *
     * @param command The command to send.
     * @param params Any parameters the command requires.
     */
    fun command(command: Int, vararg params: Int)

    /**
     * Send pixel data to the display.
     *
     * @param data The data to send.
     */
    fun data(data: ByteArray)
}
