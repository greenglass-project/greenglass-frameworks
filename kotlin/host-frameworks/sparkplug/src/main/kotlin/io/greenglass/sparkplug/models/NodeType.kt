package io.greenglass.sparkplug.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable


@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
open class NodeType (
    val type : String,
    val name : String,
    val description : String,
    val image : String,
    open val metrics : List<Metric>
) {
    companion object
}
