package io.greenglass.sparkplug.models

import io.greenglass.sparkplug.models.Metric
import io.greenglass.sparkplug.models.NodeType


class PhysicalNode(val nodeId : String,
                   type : String,
                   name : String,
                   description : String,
                   image : String,
                   metrics : List<Metric>
) : NodeType(type, name, description, image, metrics) {
    constructor(nodeId : String, nodeType : NodeType) : this(
        nodeId,
        nodeType.type,
        nodeType.name,
        nodeType.description,
        nodeType.image,
        nodeType.metrics
    )
}
