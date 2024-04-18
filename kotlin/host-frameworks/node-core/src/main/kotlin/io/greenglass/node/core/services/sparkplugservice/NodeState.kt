package io.greenglass.node.core.services.sparkplugservice

enum class NodeState(name: String, val value: Int) {
    Offline("Offline", 0),
    ConnectingToBroker("Connecting to broker", 1),
    WaitingForHostState("Waiting for host", 2),
    Online("Online", 3)
}
