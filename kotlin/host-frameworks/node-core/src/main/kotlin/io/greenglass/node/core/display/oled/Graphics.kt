package io.greenglass.node.core.display.oled

import io.greenglass.node.core.display.oled.font.Font
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.nio.charset.Charset

/**
 * A wrapper around the SSD1306 with some basic graphics methods.
 *
 * @author fauxpark
 */
class Graphics
/**
 * Graphics constructor.
 *
 * @param ssd1306 The SSD1306 object to use.
 */ internal constructor(
    /**
     * The SSD1306 OLED display.
     */
    private val ssd1306: SSD1306
) {
    /**
     * Draw text onto the display.
     *
     * @param x The X position to start drawing at.
     * @param y The Y position to start drawing at.
     * @param font The font to use.
     * @param text The text to draw.
     */
    fun text(x: Int, y: Int, font: Font, text: String) {
        var x = x
        val rows: Int = font.rows
        val cols: Int = font.columns
        val glyphs: IntArray = font.glyphs
        val bytes = text.toByteArray(Charset.forName(font.name))
        for (i in 0 until text.length) {
            var p = (bytes[i].toInt() and 0xFF) * cols
            for (col in 0 until cols) {
                var mask = glyphs[p++]
                for (row in 0 until rows) {
                    ssd1306.setPixel(x, y + row, mask and 1 == 1)
                    mask = mask shr 1
                }
                x++
            }
            x++
        }
    }

    /**
     * Draw an image onto the display.
     *
     * @param image The image to draw.
     * @param x The X position of the image.
     * @param y The Y position of the image.
     * @param width The width to resize the image to.
     * @param height The height to resize the image to.
     */
    fun image(image: BufferedImage?, x: Int, y: Int, width: Int, height: Int) {
        val mono = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
        mono.createGraphics().drawImage(image, 0, 0, width, height, null)
        val r: Raster = mono.raster
        for (i in 0 until height) {
            for (j in 0 until width) {
                ssd1306.setPixel(x + j, y + i, r.getSample(j, i, 0) > 0)
            }
        }
    }

    /**
     * Draw a line from one point to another.
     *
     * @param x0 The X position of the first point.
     * @param y0 The Y position of the first point.
     * @param x1 The X position of the second point.
     * @param y1 The Y position of the second point.
     */
    fun line(xx0: Int, yy0: Int, xx1: Int, yy1: Int) {
        var x0 = xx0
        var y0 = yy0
        var x1 = xx1
        var y1 = yy1
        var dx = x1 - x0
        var dy = y1 - y0
        if (dx == 0 && dy == 0) {
            ssd1306.setPixel(x0, y0, true)
            return
        }
        if (dx == 0) {
            for (y in Math.min(y0, y1)..Math.max(y1, y0)) {
                ssd1306.setPixel(x0, y, true)
            }
        } else if (dy == 0) {
            for (x in Math.min(x0, x1)..Math.max(x1, x0)) {
                ssd1306.setPixel(x, y0, true)
            }
        } else if (Math.abs(dx) >= Math.abs(dy)) {
            if (dx < 0) {
                val ox = x0
                val oy = y0
                x0 = x1
                y0 = y1
                x1 = ox
                y1 = oy
                dx = x1 - x0
                dy = y1 - y0
            }
            val coeff = dy.toDouble() / dx.toDouble()
            for (x in 0..dx) {
                ssd1306.setPixel(x + x0, y0 + Math.round(x * coeff).toInt(), true)
            }
        } else if (Math.abs(dx) < Math.abs(dy)) {
            if (dy < 0) {
                val ox = x0
                val oy = y0
                x0 = x1
                y0 = y1
                x1 = ox
                y1 = oy
                dx = x1 - x0
                dy = y1 - y0
            }
            val coeff = dx.toDouble() / dy.toDouble()
            for (y in 0..dy) {
                ssd1306.setPixel(x0 + Math.round(y * coeff).toInt(), y + y0, true)
            }
        }
    }

    /**
     * Draw a rectangle.
     *
     * @param x The X position of the rectangle.
     * @param y The Y position of the rectangle.
     * @param width The width of the rectangle in pixels.
     * @param height The height of the rectangle in pixels.
     * @param fill Whether to draw a filled rectangle.
     */
    fun rectangle(x: Int, y: Int, width: Int, height: Int, fill: Boolean) {
        if (fill) {
            for (i in 0 until width) {
                for (j in 0 until height) {
                    ssd1306.setPixel(x + i, y + j, true)
                }
            }
        } else if (width > 0 && height > 0) {
            line(x, y, x, y + height - 1)
            line(x, y + height - 1, x + width - 1, y + height - 1)
            line(x + width - 1, y + height - 1, x + width - 1, y)
            line(x + width - 1, y, x, y)
        }
    }

    /**
     * Draw an arc.
     *
     * @param x The X position of the center of the arc.
     * @param y The Y position of the center of the arc.
     * @param radius The radius of the arc in pixels.
     * @param startAngle The starting angle of the arc in degrees.
     * @param endAngle The ending angle of the arc in degrees.
     */
    fun arc(x: Int, y: Int, radius: Int, startAngle: Int, endAngle: Int) {
        for (i in startAngle..endAngle) {
            ssd1306.setPixel(
                x + Math.round(radius * Math.sin(Math.toRadians(i.toDouble()))).toInt(), y + Math.round(
                    radius * Math.cos(
                        Math.toRadians(i.toDouble())
                    )
                ).toInt(), true
            )
        }
    }

    /**
     * Draw a circle.
     * This is the same as calling arc() with a start and end angle of 0 and 360 respectively.
     *
     * @param x The X position of the center of the circle.
     * @param y The Y position of the center of the circle.
     * @param radius The radius of the circle in pixels.
     */
    fun circle(x: Int, y: Int, radius: Int) {
        arc(x, y, radius, 0, 360)
    }
}
