/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.neo4j

import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Transaction

interface GraphNode {
    fun persist(tx : Transaction) : Node

    companion object {
        val singleton = "singleton"
    }
}