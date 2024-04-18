package io.greenglass.node.core.services

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.sse.SseClient
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import io.klogging.NoCoLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.jetty.websocket.api.Session
import java.util.concurrent.ConcurrentLinkedQueue

@Serializable class StringValue(val value:String)
@Serializable class IntValue(val value:Int)
@Serializable class DoubleValue(val value:Double)
@Serializable class BoolValue(val value:Boolean)

class WebService(private val port : Int,
                 private val contextRoot : String,
                 private val persistence : PersistenceService,
                 private val backgroundScope : CoroutineScope
) : NoCoLogging {

    private val javalin: Javalin =  Javalin.create() { config ->
        config.staticFiles.add { staticFiles ->
            staticFiles.hostedPath = "/"
            staticFiles.directory = contextRoot
            staticFiles.location = Location.EXTERNAL
            staticFiles.precompress = false
            staticFiles.aliasCheck = null
        }
    }
    data class SsePublisherCtx(val  path : String, val clients : ConcurrentLinkedQueue<SseClient>, val flow : StateFlow<String>)
    data class WsPublisherCtx(val  path : String, val clients : HashMap<Session, WsContext>, val flow : StateFlow<String>)

    private val ssePublishers : HashMap<String, SsePublisherCtx> = hashMapOf()
    private val wsPublishers : HashMap<String, WsPublisherCtx> = hashMapOf()

    fun addEventPublisherX(path : String, flow : StateFlow<String>) {
        logger.info { "WEBSERVICE Registering SSE Publisher on $path" }

        val clients = ConcurrentLinkedQueue<SseClient>()
        ssePublishers[path] = SsePublisherCtx(path,clients,flow)

        javalin.sse(path) { client ->
            val ctx = ssePublishers[path]!!
            logger.info { "WEBSERVICE SSE Client connected" }
            client.keepAlive()
            client.onClose { ctx.clients.remove(client) }
            ctx.clients.add(client)
            client.sendEvent(ctx.flow.value)
        }
        backgroundScope.launch {
            flow.collect { e ->
                logger.info { "WEBSERVICE event to send $e - clients ${clients.size}"}
                ssePublishers[path]!!.clients.forEach { c -> c.sendEvent(e)}}
        }
    }

    fun addEventPublisher(path : String, flow : StateFlow<String>) {
        wsPublishers[path] = WsPublisherCtx(path, hashMapOf(), flow)
        backgroundScope.launch {
            flow.collect { e ->
                val clients = wsPublishers[path]!!.clients.entries
                logger.info { "WEBSERVICE event to send $e - clients ${clients.size} path $path"}
                clients.filter { c -> c.key.isOpen }.forEach { d -> d.value.send(e) }
            }
        }

        logger.info { "WEBSERVICE Registering WebSocket Publisher on $path" }
        javalin.ws(path) { ws ->
            ws.onConnect { ctx ->
                logger.info { "WEBSOCKET $path CONNECTED"}
                wsPublishers[path]!!.clients[ctx.session] = ctx
                ctx.send(flow.value)
            }
            ws.onMessage { ctx ->
                logger.info { "WEBSOCKET $path RECEIVED MESSAGE"}
            }
            ws.onClose { ctx ->
                logger.info { "WEBSOCKET $path CLOSED"}
                wsPublishers[path]!!.clients.remove(ctx.session)
            }
        }
    }


    fun addGet(path : String, handle : (Context) -> Unit ) {
        logger.info { "WEBSERVICE Registering GET on $path" }
        javalin.get(path, handle)
    }
    fun addPut(path : String, handle : (Context) -> Unit ) {
        logger.info { "WEBSERVICE Registering PUT on $path" }
        javalin.put(path, handle)
    }
    fun addPost(path : String, handle : (Context) -> Unit ) {
        logger.info { "WEBSERVICE Registering POST on $path" }
        javalin.post(path, handle)
    }

    fun start() {
        javalin.start(port)
    }
}


