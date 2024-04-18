/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.nats

class Topic(topic : String ) {
    private val parts : List<String>

    init { parts = topic.split(".") }

    fun get(index: Int): String {
        check(index < parts.size, lazyMessage = { "No element found at index $index" })
        return parts[index]
    }

}