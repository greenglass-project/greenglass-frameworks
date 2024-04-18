package io.greenglass.node.core.display.oled

import io.greenglass.node.core.display.oled.transport.Transport
import kotlin.experimental.inv

/**
 * An SSD1306 OLED display.
 *
 * @author fauxpark
 */
class SSD1306(
    /**
     * The width of the display in pixels.
     */
    val width: Int,
    /**
     * The height of the display in pixels.
     */
    val height: Int,
    /**
     * The transport to use.
     */
    private val transport: Transport
) {
    /**
     * A helper class for drawing lines, shapes, text and images.
     */
    var graphics: Graphics? = null
        /**
         * Get the Graphics instance, creating it if necessary.
         *
         * @return The Graphics instance.
         */
        get() {
            if (field == null) {
                field = Graphics(this)
            }
            return field
        }
        private set
    /**
     * Get the width of the display.
     *
     * @return The display width in pixels.
     */
    /**
     * Get the height of the display.
     *
     * @return The display height in pixels.
     */

    /**
     * The number of pages in the display.
     */
    private val pages: Int = height / 8
    /**
     * Get the display buffer.
     *
     * @return The display buffer.
     */
    /**
     * Set the display buffer.
     *
     * @param buffer The buffer to set.
     */
    /**
     * The display buffer.
     */
    var buffer: ByteArray
    /**
     * Get the initialised state of the display.
     */
    /**
     * Indicates whether the display has been started up.
     */
    var isInitialised = false
        private set
    /**
     * Get the lower column start address for page addressing mode.
     *
     * @return The lower column start address, from 0 to 15.
     */
    /**
     * The lower column start address for page addressing mode.
     */
    var lowerColStart = 0
        /**
         * Set the lower column start address for page addressing mode.
         *
         * @param lowerColStart The lower column start address, from 0 to 15. Values outside this range will be clamped.
         */
        set(lowerColStart) {
            var l = lowerColStart
            l = clamp(0, 15, l)
            field = l
            command(Command.SET_LOWER_COL_START or l)
        }
    /**
     * Get the higher column start address for page addressing mode.
     *
     * @return The higher column start address, from 0 to 15.
     */
    /**
     * The higher column start address for page addressing mode.
     */
    var higherColStart = 0
        /**
         * Set the higher column start address for page addressing mode.
         *
         * @param higherColStart The higher column start address, from 0 to 15. Values outside this range will be clamped.
         */
        set(higherColStart) {
            val h = clamp(0, 15, higherColStart)
            field = h
            command(Command.SET_HIGHER_COL_START or h)
        }
    /**
     * Get the memory addressing mode.
     *
     * @return The current memory mode, either [Constant.MEMORY_MODE_HORIZONTAL], [Constant.MEMORY_MODE_VERTICAL], or [Constant.MEMORY_MODE_PAGE].
     */
    /**
     * The memory addressing mode of the display.
     */
    var memoryMode = 0
        /**
         * Set the memory addressing mode.
         *
         * @param memoryMode The memory mode to set. Must be one of [Constant.MEMORY_MODE_HORIZONTAL], [Constant.MEMORY_MODE_VERTICAL], or [Constant.MEMORY_MODE_PAGE].
         */
        set(memoryMode) {
            if (memoryMode == Constant.MEMORY_MODE_HORIZONTAL || memoryMode == Constant.MEMORY_MODE_VERTICAL || memoryMode == Constant.MEMORY_MODE_PAGE) {
                field = memoryMode
                command(Command.SET_MEMORY_MODE, memoryMode)
            }
        }
    /**
     * Get the scrolling state of the display.
     *
     * @return Whether the display is scrolling.
     */
    /**
     * Indicates whether the display is currently scrolling.
     */
    var isScrolling = false
        private set
    /**
     * Get the display start line.
     *
     * @return The row to begin displaying at.
     */
    /**
     * The starting row of the display buffer.
     */
    var startLine = 0
        /**
         * Set the display start line.
         *
         * @param startLine The row to begin displaying at.
         */
        set(startLine) {
            val s = clamp(0, height - 1, startLine)
            field = s
            command(Command.SET_START_LINE or s)
        }
    /**
     * Get the display contrast.
     *
     * @return The current contrast level of the display.
     */
    /**
     * The current contrast level of the display.
     */
    var contrast = 0
        /**
         * Set the display contrast.
         *
         * @param contrast The contrast to set, from 0 to 255. Values outside of this range will be clamped.
         */
        set(contrast) {
            val c = clamp(0, 255, contrast)
            field = contrast
            command(Command.SET_CONTRAST, c)
        }
    /**
     * Get the horizontal flip state of the display.
     *
     * @return Whether the display is horizontally flipped.
     */
    /**
     * Indicates whether the display is horizontally flipped.
     */
    var isHFlipped = false
        /**
         * Flip the display horizontally.
         *
         * @param hFlipped Whether to flip the display or return to normal.
         */
        set(hFlipped) {
            field = hFlipped
            if (hFlipped) {
                command(Command.SET_SEGMENT_REMAP)
            } else {
                command(Command.SET_SEGMENT_REMAP_REVERSE)
            }

            // Horizontal flipping is not immediate
            display()
        }
    /**
     * Get the inverted state of the display.
     *
     * @return Whether the display is inverted or not.
     */
    /**
     * Indicates whether the display is inverted.
     */
    var isInverted = false
        /**
         * Invert the display.
         * When inverted, an "on" bit in the buffer results in an unlit pixel.
         *
         * @param inverted Whether to invert the display or return to normal.
         */
        set(inverted) {
            field = inverted
            command(if (inverted) Command.INVERT_DISPLAY else Command.NORMAL_DISPLAY)
        }
    /**
     * Get the display state.
     *
     * @return True if the display is on.
     */
    /**
     * Indicates whether the display is on or off.
     */
    var isDisplayOn = false
        /**
         * Turn the display on or off.
         *
         * @param displayOn Whether to turn the display on.
         */
        set(displayOn) {
            field = displayOn
            if (displayOn) {
                command(Command.DISPLAY_ON)
            } else {
                command(Command.DISPLAY_OFF)
            }
        }
    /**
     * Get the starting page for page addressing mode.
     *
     * @return The page to begin displaying at, from 0 to 7.
     */
    /**
     * The starting page of the display for page addressing mode.
     */
    var startPage = 0
        /**
         * Set the starting page for page addressing mode.
         *
         * @param startPage The page to begin displaying at, from 0 to 7. Values outside this range will be clamped.
         */
        set(startPage) {
            val s = clamp(0, 7, startPage)
            field = s
            command(Command.SET_PAGE_START_ADDR or s)
        }
    /**
     * Get the vertical flip state of the display.
     *
     * @return Whether the display is vertically flipped.
     */
    /**
     * Indicates whether the display is vertically flipped.
     */
    var isVFlipped = false
        /**
         * Flip the display vertically.
         *
         * @param vFlipped Whether to flip the display or return to normal.
         */
        set(vFlipped) {
            field = vFlipped
            if (vFlipped) {
                command(Command.SET_COM_SCAN_INC)
            } else {
                command(Command.SET_COM_SCAN_DEC)
            }
        }
    /**
     * Get the display offset.
     *
     * @return The number of rows the display is offset by.
     */
    /**
     * The current display offset.
     */
    var offset = 0
        /**
         * Set the display offset.
         *
         * @param offset The number of rows to offset the display by. Values outside of this range will be clamped.
         */
        set(offset) {
            val o = clamp(0, height - 1, offset)
            field = o
            command(Command.SET_DISPLAY_OFFSET, o)
        }

    /**
     * The hardware configuration of the display's COM pins.
     */
    private var comPins = 0

    /**
     * SSD1306 constructor.
     *
     * @param width The width of the display in pixels.
     * @param height The height of the display in pixels.
     * @param transport The transport to use.
     */
    init {
        buffer = ByteArray(width * pages)
    }

    /**
     * Start the power on procedure for the display.
     *
     * @param externalVcc Indicates whether the display is being driven by an external power source.
     */
    fun startup(externalVcc: Boolean) {
        reset()
        isDisplayOn = false
        command(Command.SET_DISPLAY_CLOCK_DIV, width)
        command(Command.SET_MULTIPLEX_RATIO, height - 1)
        offset = 0
        startLine = 0
        command(Command.SET_CHARGE_PUMP, if (externalVcc) Constant.CHARGE_PUMP_DISABLE else Constant.CHARGE_PUMP_ENABLE)
        memoryMode = Constant.MEMORY_MODE_HORIZONTAL
        isHFlipped = false
        isVFlipped = false
        cOMPinsConfiguration = if (height == 64) Constant.COM_PINS_ALTERNATING else Constant.COM_PINS_SEQUENTIAL
        contrast = if (externalVcc) 0x9F else 0xCF
        command(Command.SET_PRECHARGE_PERIOD, if (externalVcc) 0x22 else 0xF1)
        command(Command.SET_VCOMH_DESELECT, Constant.VCOMH_DESELECT_LEVEL_00)
        command(Command.DISPLAY_ALL_ON_RESUME)
        isInverted = false
        isDisplayOn = true
        clear()
        display()
        isInitialised = true
    }

    /**
     * Start the power off procedure for the display.
     */
    fun shutdown() {
        clear()
        display()
        isDisplayOn = false
        reset()
        isInverted = false
        isHFlipped = false
        isVFlipped = false
        stopScroll()
        contrast = 0
        offset = 0
        transport.shutdown()
        isInitialised = false
    }

    /**
     * Reset the display.
     */
    fun reset() {
        transport.reset()
    }

    /**
     * Clear the buffer.
     * <br></br>
     * NOTE: This does not clear the display, you must manually call [.display].
     */
    fun clear() {
        buffer = ByteArray(width * pages)
    }

    /**
     * Send the buffer to the display.
     */
    @Synchronized
    fun display() {
        command(Command.SET_COLUMN_ADDRESS, 0, width - 1)
        command(Command.SET_PAGE_ADDRESS, 0, pages - 1)
        data(buffer)

        // Jump start scrolling again if new data is written while enabled
        if (isScrolling) {
            noOp()
        }
    }

    /**
     * Scroll the display horizontally.
     *
     * @param direction The direction to scroll, where a value of true results in the display scrolling to the left.
     * @param start The start page address, from 0 to 7.
     * @param end The end page address, from 0 to 7.
     * @param speed The scrolling speed (scroll step).
     *
     * @see Constant.SCROLL_STEP_5
     */
    fun scrollHorizontally(direction: Boolean, start: Int, end: Int, speed: Int) {
        command(
            if (direction) Command.LEFT_HORIZONTAL_SCROLL else Command.RIGHT_HORIZONTAL_SCROLL,
            Constant.DUMMY_BYTE_00,
            start,
            speed,
            end,
            Constant.DUMMY_BYTE_00,
            Constant.DUMMY_BYTE_FF
        )
    }

    /**
     * Scroll the display horizontally and vertically.
     *
     * @param direction The direction to scroll, where a value of true results in the display scrolling to the left.
     * @param start The start page address, from 0 to 7.
     * @param end The end page address, from 0 to 7.
     * @param offset The number of rows from the top to start the vertical scroll area at.
     * @param rows The number of rows in the vertical scroll area.
     * @param speed The scrolling speed (scroll step).
     * @param step The number of rows to scroll vertically each frame.
     *
     * @see Constant.SCROLL_STEP_5
     */
    fun scrollDiagonally(direction: Boolean, start: Int, end: Int, offset: Int, rows: Int, speed: Int, step: Int) {
        command(Command.SET_VERTICAL_SCROLL_AREA, offset, rows)
        command(
            if (direction) Command.VERTICAL_AND_LEFT_HORIZONTAL_SCROLL else Command.VERTICAL_AND_RIGHT_HORIZONTAL_SCROLL,
            Constant.DUMMY_BYTE_00,
            start,
            speed,
            end,
            step
        )
    }

    /**
     * Stop scrolling the display.
     */
    fun stopScroll() {
        isScrolling = false
        command(Command.DEACTIVATE_SCROLL)
    }

    /**
     * Start scrolling the display.
     */
    fun startScroll() {
        isScrolling = true
        command(Command.ACTIVATE_SCROLL)
    }

    var cOMPinsConfiguration: Int
        /**
         * Get hardware configuration of the display's COM pins.
         *
         * @return The COM pins configuration, one of [Constant.COM_PINS_SEQUENTIAL], [Constant.COM_PINS_SEQUENTIAL_LR], [Constant.COM_PINS_ALTERNATING] or [Constant.COM_PINS_ALTERNATING_LR].
         */
        get() = comPins
        /**
         * Set the hardware configuration of the display's COM pins.
         *
         * @param comPins The COM pins configuration. Must be one of [Constant.COM_PINS_SEQUENTIAL], [Constant.COM_PINS_SEQUENTIAL_LR], [Constant.COM_PINS_ALTERNATING] or [Constant.COM_PINS_ALTERNATING_LR].
         */
        set(comPins) {
            if (comPins == Constant.COM_PINS_SEQUENTIAL || comPins == Constant.COM_PINS_SEQUENTIAL_LR || comPins == Constant.COM_PINS_ALTERNATING || comPins == Constant.COM_PINS_ALTERNATING_LR) {
                this.comPins = comPins
                command(Command.SET_COM_PINS, comPins)
            }
        }

    /**
     * No operation.
     */
    fun noOp() {
        command(Command.NOOP)
    }

    /**
     * Get a pixel in the buffer.
     *
     * @param x The X position of the pixel to set.
     * @param y The Y position of the pixel to set.
     *
     * @return False if the pixel is "off" or the given coordinates are out of bounds, true if the pixel is "on".
     */
    fun getPixel(x: Int, y: Int): Boolean {
        return if (x < 0 || x >= width || y < 0 || y >= height) {
            false
        } else buffer[x + y / 8 * width].toInt() and (1 shl (y and 7)) != 0
    }

    /**
     * Set a pixel in the buffer.
     *
     * @param x The X position of the pixel to set.
     * @param y The Y position of the pixel to set.
     * @param on Whether to turn this pixel on or off.
     *
     * @return False if the given coordinates are out of bounds.
     */
    fun setPixel(x: Int, y: Int, on: Boolean): Boolean {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false
        }
        if (on) {
            buffer[x + y / 8 * width] =
                (buffer[x + y / 8 * width].toInt() or (1 shl (y and 7)).toByte().toInt()).toByte()
        } else {
            buffer[x + y / 8 * width] =
                (buffer[x + y / 8 * width].toInt() and (1 shl (y and 7)).toByte().inv().toInt()).toByte()
        }
        return true
    }

    /**
     * Send a command to the display.
     *
     * @param command The command to send.
     * @param params Any parameters the command requires.
     */
    fun command(command: Int, vararg params: Int) {
        transport.command(command, *params)
    }

    /**
     * Send pixel data to the display.
     *
     * @param data The data to send.
     */
    fun data(data: ByteArray) {
        transport.data(data)
    }

    /**
     * Clamp the given value to a specified range.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @param value The value to clamp.
     *
     * @return The value clamped to the minimum and maximum values.
     */
    private fun clamp(min: Int, max: Int, value: Int): Int {
        if (value < min) {
            return min
        } else if (value > max) {
            return max
        }
        return value
    }
}
