package io.greenglass.node.core.models

import io.greenglass.node.sparkplug.datatypes.MetricDefinition
import kotlinx.serialization.Serializable

@Serializable
class NodeDefinition (
    val type : String,
    val name : String,
    val description : String,
    val image : String,
    val metrics : List<MetricDefinition>,
    val drivers : List<Driver>
)
