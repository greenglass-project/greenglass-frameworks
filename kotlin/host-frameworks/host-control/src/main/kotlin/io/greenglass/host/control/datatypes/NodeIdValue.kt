package io.greenglass.host.control.datatypes

import io.greenglass.host.control.models.variableidentifiers.EonNodeId

class NodeIdValue(val nodeName : String, val nodeId : String?) {
    constructor(n : EonNodeId) : this(
        nodeName = n.nodeName,
        nodeId = n.nodeId
    )
}
