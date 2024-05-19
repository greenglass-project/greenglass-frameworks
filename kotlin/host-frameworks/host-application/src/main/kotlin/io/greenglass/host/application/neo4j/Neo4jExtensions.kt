/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.neo4j

import io.github.oshai.kotlinlogging.KotlinLogging
import org.neo4j.graphdb.*
import org.neo4j.graphdb.traversal.Evaluation
import org.neo4j.graphdb.traversal.Evaluator
import org.neo4j.graphdb.traversal.Uniqueness
import io.greenglass.host.application.error.Result
fun getOneNodeWithRelationships(
    node1: Node,
    rel1: RelationshipType,
    dir1: Direction,
    node2: Node,
    rel2: RelationshipType,
    dir2: Direction,
) : Node? {
    val set1 = if(dir1 == Direction.OUTGOING)
        node1.getRelationships(dir1, rel1).map { r -> r.endNode }
    else
        node1.getRelationships(dir1, rel1).map { r -> r.startNode }
    val set2 = if(dir2 == Direction.OUTGOING)
        node2.getRelationships(dir2, rel2).map { r -> r.endNode }
    else
        node2.getRelationships(dir2, rel2).map { r -> r.startNode }
    return if(set1.isNotEmpty() && set2.isNotEmpty())
        set1.intersect(set2.toSet()).first()
    else
        null
}

fun getNodesWithRelationships(
    node1: Node,
    rel1: RelationshipType,
    dir1: Direction,
    node2: Node,
    rel2: RelationshipType,
    dir2: Direction,
) : List<Node> {
    val set1 = if(dir1 == Direction.OUTGOING)
        node1.getRelationships(dir1, rel1).map { r -> r.endNode }
    else
        node1.getRelationships(dir1, rel1).map { r -> r.startNode }
    val set2 = if(dir2 == Direction.OUTGOING)
        node2.getRelationships(dir2, rel2).map { r -> r.endNode }
    else
        node2.getRelationships(dir2, rel2).map { r -> r.startNode }
    return if(set1.isNotEmpty() && set2.isNotEmpty())
        set1.intersect(set2.toSet()).toList()
    else
        listOf()
}

fun Node.getRelationshipsToNodeType(direction : Direction, type : RelationshipType, label : Label ) : List<Relationship> {
     return if(direction == Direction.OUTGOING)
            this.getRelationships(direction, type).filter { r -> r.endNode.hasLabel(label) }
        else
            this.getRelationships(direction, type).filter { r -> r.startNode.hasLabel(label) }
    }

fun Node.getSingleRelationshipToNodeType(direction : Direction, type : RelationshipType, label : Label ) : Relationship? {
    return if(direction == Direction.OUTGOING)
        this.getRelationships(direction, type).firstOrNull { r -> r.endNode.hasLabel(label) }
    else
        this.getRelationships(direction, type).firstOrNull { r -> r.startNode.hasLabel(label) }
}

fun Node.getSingleRelationshipToNodeType(direction : Direction, label : Label ) : Relationship? {
    return if(direction == Direction.OUTGOING)
        this.getRelationships(direction).firstOrNull { r -> r.endNode.hasLabel(label) }
    else
        this.getRelationships(direction).firstOrNull { r -> r.startNode.hasLabel(label) }
}


fun Node.getSingleRelationshipToNodeType(direction : Direction,
                                         type : RelationshipType,
                                         label : Label,
                                         property : String,
                                         value : Any) : Relationship? {
    return if (direction == Direction.OUTGOING)
        this.getRelationships(direction, type)
            .firstOrNull { r -> r.endNode.hasLabel(label) && r.endNode.getProperty(property).equals(value) }
    else
        this.getRelationships(direction, type)
            .firstOrNull { r -> r.startNode.hasLabel(label) && r.startNode.getProperty(property).equals(value) }
}

fun Node.getSingleRelationshipToNodeType(direction : Direction,
                                         label : Label,
                                         property : String,
                                         value : Any) : Relationship? {
    return if (direction == Direction.OUTGOING)
        this.getRelationships(direction)
            .firstOrNull { r -> r.endNode.hasLabel(label) && r.endNode.getProperty(property).equals(value) }
    else
        this.getRelationships(direction)
            .firstOrNull { r -> r.startNode.hasLabel(label) && r.startNode.getProperty(property).equals(value) }
}
/**
 * Transaction.useOrError
 *
 * replacement for the 'use' function that catches any exception and
 * calls a handler for it
 * @param func
 * @param onError
 */
fun Transaction.useOrError(func : ()-> Unit, onError: (Throwable) -> Unit) {
    try {
        this.use { func() }
    } catch(t : Throwable) {
        onError(t)
    }
}
/*fun Node.onExists() {
    if(this != null) {

    }
}*/

fun Transaction.findPath(nodeA : Node, nodeB : Node, vararg rels : RelationshipType) : List<Node> {
    val logger = KotlinLogging.logger {}

    val orderedPathContext = rels.asList()
        val td = this.traversalDescription().uniqueness( Uniqueness.NODE_PATH ).evaluator(object : Evaluator {
            override fun evaluate(path: Path) : Evaluation {
                logger.debug { "End node = ${nodeB.labels.first().name()} current node = ${path.endNode().labels.first().name()}" }
                logger.debug { "Path length = ${path.length()}"}

                val endNodeIsTarget = path.endNode().equals(nodeB)
                logger.debug { "FOUND = $endNodeIsTarget"}
                return Evaluation.of(endNodeIsTarget, !endNodeIsTarget)
            }
        })

        return td.traverse(nodeA).nodes().toList()
}


