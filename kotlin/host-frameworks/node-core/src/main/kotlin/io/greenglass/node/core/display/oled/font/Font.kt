package io.greenglass.node.core.display.oled.font

/**
 * A basic interface to facilitate font selection in text drawing methods.
 * <br></br>
 * These fonts generally include 256 glyphs, comprised of *columns* integer values containing *rows* bits of information.
 * An "on" bit in the value represents an "on" bit in the display RAM (and thus, in normal display mode, a lit pixel).
 * The top of a glyph is the least significant bit of each column.
 *
 * @author fauxpark
 */
interface Font {
    /**
     * Get the name of the font's character set.
     *
     * @return The font's character set name.
     */
    val name: String

    /**
     * Get the number of columns in the font.
     *
     * @return The font's column count.
     */
    val columns: Int

    /**
     * Get the number of rows in the font.
     *
     * @return The font's row count.
     */
    val rows: Int

    /**
     * Get the glyph data for the font.
     *
     * @return An array of ints representing the columns for each glyph.
     */
    val glyphs: IntArray
}
