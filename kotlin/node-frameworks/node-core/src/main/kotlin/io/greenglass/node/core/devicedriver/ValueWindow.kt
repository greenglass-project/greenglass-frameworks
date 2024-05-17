/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.devicedriver

import io.klogging.NoCoLogging
import java.math.RoundingMode

class ValueWindow(private var size : Int, private val rounding : Int) : NoCoLogging {
    private var index = 0
    private val window : ArrayList<Double> = arrayListOf()

    fun addValue(value : Double) : Double? {
        logger.debug { "ValueWindow add value $value index = $index" }
        return when {
            size == 0 -> value
            (index == size-1) -> {
                window.add(value)
                val avg = (window.sum() / size).toBigDecimal().setScale(rounding, RoundingMode.HALF_UP).toDouble()
                window.clear()
                index = 0
                logger.debug { "ValueWindow AVERAGE = $avg" }
                avg
            }
            else -> {
                window.add(value)
                ++index
                null
            }
        }
    }

    fun setWindowSize(size : Int) {
        this.size = size
        window.clear()
        index = 0
    }
}