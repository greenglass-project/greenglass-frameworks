package io.greenglass.node.core.services

import io.klogging.NoCoLogging
import java.net.InetAddress
import javax.jmdns.JmDNS
import javax.jmdns.ServiceInfo

class MdnsService(val service : String, val port : Int,  val secure : Boolean) : NoCoLogging {

    private val jmdns = JmDNS.create(InetAddress.getLocalHost())

    fun run(nodeId : String,) {
        val serviceInfo = ServiceInfo.create(
            "_http._tcp.local.",
            service,
            port,
            "nodeid=$nodeId"
        )
        logger.info { "Registering zeroconf service $service for node-id $nodeId"}
        jmdns.registerService(serviceInfo);
    }
}