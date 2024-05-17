package io.greenglass.host.control.sparkplug.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

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
