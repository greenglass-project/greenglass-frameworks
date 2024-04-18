package io.greenglass.node.sparkplug.datatypes

import kotlinx.serialization.Serializable

@Serializable
data class MetricIdentifier(val nodeId : String, val metricName : String)