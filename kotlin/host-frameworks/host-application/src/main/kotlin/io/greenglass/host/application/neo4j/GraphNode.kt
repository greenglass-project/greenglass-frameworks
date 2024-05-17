package io.greenglass.host.application.neo4j

import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Transaction

interface GraphNode {
    fun persist(tx : Transaction) : Node

    companion object {
        val singleton = "singleton"
    }
}