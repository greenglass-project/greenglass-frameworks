package io.greenglass.host.control.datatypes

enum class NodeState{
    NotBound,
    Offline,
    Online
}
data class NodeStateValue(val nodeName : String, val state : NodeState)