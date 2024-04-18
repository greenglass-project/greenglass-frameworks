package io.greenglass.node.core.display.oled

/**
 * This class defines the commands that can be sent to the SSD1306.
 * Some of them are standalone commands and others require arguments following them.
 * <br></br>
 * Please refer to [the SSD1306 datasheet](https://www.adafruit.com/datasheets/SSD1306.pdf)
 * for more information.
 *
 * @author fauxpark
 */
object Command {
    /**
     * Set the lower column start address for page addressing mode.
     * OR this command with 0x00 to 0x0F (0 to 15) to set the desired value.
     */
    const val SET_LOWER_COL_START = 0x00

    /**
     * Set the higher column start address for page addressing mode.
     * OR this command with 0x00 to 0x0F (0 to 15) to set the desired value.
     */
    const val SET_HIGHER_COL_START = 0x10

    /**
     * Set the memory addressing mode.
     *
     * @see Constant.MEMORY_MODE_HORIZONTAL
     *
     * @see Constant.MEMORY_MODE_VERTICAL
     *
     * @see Constant.MEMORY_MODE_PAGE
     */
    const val SET_MEMORY_MODE = 0x20

    /**
     * Set the column start and end address of the display.
     */
    const val SET_COLUMN_ADDRESS = 0x21

    /**
     * Set the page start and end address of the display.
     */
    const val SET_PAGE_ADDRESS = 0x22

    /**
     * Set the display to scroll to the right.
     */
    const val RIGHT_HORIZONTAL_SCROLL = 0x26

    /**
     * Set the display to scroll to the left.
     */
    const val LEFT_HORIZONTAL_SCROLL = 0x27

    /**
     * Set the display to scroll vertically and to the right.
     */
    const val VERTICAL_AND_RIGHT_HORIZONTAL_SCROLL = 0x29

    /**
     * Set the display to scroll vertically and to the left.
     */
    const val VERTICAL_AND_LEFT_HORIZONTAL_SCROLL = 0x2A

    /**
     * Turn off scrolling of the display.
     */
    const val DEACTIVATE_SCROLL = 0x2E

    /**
     * Turn on scrolling of the display.
     */
    const val ACTIVATE_SCROLL = 0x2F

    /**
     * Set the starting row of the display buffer.
     * OR this command with 0x00 to 0x3F (0 to 63) to set the desired value.
     */
    const val SET_START_LINE = 0x40

    /**
     * Set the contrast of the display.
     */
    const val SET_CONTRAST = 0x81

    /**
     * Sets the charge pump regulator state.
     *
     * @see Constant.CHARGE_PUMP_DISABLE
     *
     * @see Constant.CHARGE_PUMP_ENABLE
     */
    const val SET_CHARGE_PUMP = 0x8D

    /**
     * Map column address 0 to SEG0.
     * This command is used for horizontally flipping the display.
     */
    const val SET_SEGMENT_REMAP = 0xA0

    /**
     * Map column address 127 to SEG0.
     * This command is used for horizontally flipping the display.
     */
    const val SET_SEGMENT_REMAP_REVERSE = 0xA1

    /**
     * Set the offset and number of rows in the vertical scrolling area.
     */
    const val SET_VERTICAL_SCROLL_AREA = 0xA3

    /**
     * Turn on the display with the buffer contents.
     */
    const val DISPLAY_ALL_ON_RESUME = 0xA4

    /**
     * Turn on the entire display, ignoring the buffer contents.
     */
    const val DISPLAY_ALL_ON = 0xA5

    /**
     * Set the display to normal mode, where a 1 in the buffer represents a lit pixel.
     */
    const val NORMAL_DISPLAY = 0xA6

    /**
     * Set the display to inverse mode, where a 1 in the buffer represents an unlit pixel.
     */
    const val INVERT_DISPLAY = 0xA7

    /**
     * Set the multiplex ratio of the display.
     */
    const val SET_MULTIPLEX_RATIO = 0xA8

    /**
     * Turn the display off (sleep mode).
     */
    const val DISPLAY_OFF = 0xAE

    /**
     * Turn the display on.
     */
    const val DISPLAY_ON = 0xAF

    /**
     * Set the page start address for page addressing mode.
     * OR this command with 0x00 to 0x07 (0 to 7) to set the desired value.
     */
    const val SET_PAGE_START_ADDR = 0xB0

    /**
     * Set the row output scan direction from COM0 to COM63.
     * This command is used for vertically flipping the display.
     */
    const val SET_COM_SCAN_INC = 0xC0

    /**
     * Set the row output scan direction from COM63 to COM0.
     * This command is used for vertically flipping the display.
     */
    const val SET_COM_SCAN_DEC = 0xC8

    /**
     * Set the display offset.
     * Maps the display start line to the specified row.
     */
    const val SET_DISPLAY_OFFSET = 0xD3

    /**
     * Set the display clock divide ratio and oscillator frequency.
     * The divide ratio makes up the lower four bits.
     */
    const val SET_DISPLAY_CLOCK_DIV = 0xD5

    /**
     * Set the duration of the pre-charge period.
     */
    const val SET_PRECHARGE_PERIOD = 0xD9

    /**
     * Set the hardware configuration of the display's COM pins.
     *
     * @see Constant.COM_PINS_SEQUENTIAL
     *
     * @see Constant.COM_PINS_SEQUENTIAL_LR
     *
     * @see Constant.COM_PINS_ALTERNATING
     *
     * @see Constant.COM_PINS_ALTERNATING_LR
     */
    const val SET_COM_PINS = 0xDA

    /**
     * Adjust the `V<sub>COMH</sub>` regulator output.
     *
     * @see Constant.VCOMH_DESELECT_LEVEL_00
     *
     * @see Constant.VCOMH_DESELECT_LEVEL_20
     *
     * @see Constant.VCOMH_DESELECT_LEVEL_30
     */
    const val SET_VCOMH_DESELECT = 0xDB

    /**
     * No operation.
     */
    const val NOOP = 0xE3
}
