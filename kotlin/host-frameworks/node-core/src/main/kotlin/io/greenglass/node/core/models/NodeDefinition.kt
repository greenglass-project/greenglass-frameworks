package io.greenglass.node.core.models

import io.greenglass.sparkplug.models.NodeType
import kotlinx.serialization.Serializable

@Serializable
class NodeDefinition (
    type : String,
    name : String,
    description : String,
    image : String,
    override val metrics : List<MetricDefinition>,
    val drivers : List<Driver>
) : NodeType(type, name, description, image, metrics)
