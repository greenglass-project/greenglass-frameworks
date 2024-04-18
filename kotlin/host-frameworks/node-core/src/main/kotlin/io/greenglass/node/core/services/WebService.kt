package io.greenglass.node.core.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.models.Settings
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.sse.SseClient
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentLinkedQueue

@Serializable class StringValue(val value:String)
@Serializable class IntValue(val value:Int)
@Serializable class DoubleValue(val value:Double)
@Serializable class BoolValue(val value:Boolean)

class WebService(private val port : Int,
                 private val contextRoot : String,
                 private val nodeTypes: NodeTypesService,
                 private val persistence : PersistenceService,
                 private val backgroundScope : CoroutineScope
) {

    private val logger = KotlinLogging.logger {}

    private val settingsFlow = MutableSharedFlow<Settings>(1)

    private val nodeTypeKey = "settings/nodetype"
    private val nodeIdKey = "settings/nodeId"
    private val groupIdKey = "settings/groupId"
    private val hostIdKey = "settings/hostId"
    private val brokerKey = "settings/broker"

    private val javalin: Javalin =  Javalin.create()
    /*{ config ->
        config.staticFiles.add { staticFiles ->
            staticFiles.hostedPath = "/"
            staticFiles.directory = contextRoot
            staticFiles.location = Location.EXTERNAL
            staticFiles.precompress = false
            staticFiles.aliasCheck = null
        }
    }*/
    data class PublisherCtx(val  path : String, val clients : ConcurrentLinkedQueue<SseClient>, val flow : StateFlow<String>)
    private val eventPublishers : HashMap<String, PublisherCtx> = hashMapOf()

    init {
        addGet("/nodetypes") { ctx -> ctx.json(Json.encodeToString(nodeTypes.types())) }
        addGet("/settings") { ctx -> ctx.json(Json.encodeToString(getSettings())) }
    }

    fun subscribe() = settingsFlow.asSharedFlow()

    fun addEventPublisher(path : String, flow : StateFlow<String>) {
        logger.debug { "Registering Publisher on $path" }

        val clients = ConcurrentLinkedQueue<SseClient>()
        eventPublishers[path] = PublisherCtx(path,clients,flow)

        javalin.sse(path) { client ->
            val ctx = eventPublishers[path]!!
            logger.debug { "SSE Client connected" }
            client.keepAlive()
            client.onClose { ctx.clients.remove(client) }
            ctx.clients.add(client)
            client.sendEvent(ctx.flow.value)
        }
        backgroundScope.launch {
            flow.collect { e ->
                logger.debug { "EVENT TO SEND $e clients ${clients.size}"}
                eventPublishers[path]!!.clients.forEach { c -> c.sendEvent(e)}}
        }
    }
    fun addEndpoint(path : String, onConnect : (WsContext) -> Unit, onReceive : ((WsMessageContext) -> Unit)? = null) {
        logger.debug { "Registering Endpoint on $path" }
        javalin.ws(path) { ws ->
            ws.onConnect { ctx ->
                onConnect(ctx)
                logger.debug { "WEBSOCKET $path CONNECTED"}
            }
            ws.onMessage { ctx ->
                logger.debug { "WEBSOCKET $path RECEIVED MEDDAGE"}
                if(onReceive != null) onReceive(ctx)
            }
            ws.onClose { ctx -> logger.debug { "WEBSOCKET $path CLOSED"} }
        }
    }
    fun addGet(path : String, handle : (Context) -> Unit ) {
        logger.debug { "Registering Get on $path" }
        javalin.get(path, handle)
    }
    fun addPut(path : String, handle : (Context) -> Unit ) {
        logger.debug { "Registering Put on $path" }
        javalin.put(path, handle)
    }
    fun addPost(path : String, handle : (Context) -> Unit ) {
        logger.debug { "Registering Post on $path" }
        javalin.post(path, handle)
    }

    fun start() {
        javalin.start(port)
        settingsFlow.tryEmit(getSettings())
    }


    private fun setSettings(settings : Settings) {
        logger.debug { "nodeType : ${settings.nodeType}"}
        logger.debug { "nodeId : ${settings.nodeId}"}
        logger.debug { "groupId : ${settings.groupId}"}
        logger.debug { "hostId : ${settings.hostId}"}
        logger.debug { "broker : ${settings.broker}"}

        persistence.setString(nodeTypeKey, settings.nodeType)
        persistence.setString(nodeIdKey, settings.nodeId)
        persistence.setString(groupIdKey, settings.groupId)
        persistence.setString(hostIdKey, settings.hostId)
        persistence.setString(brokerKey, settings.broker)

        settingsFlow.tryEmit(settings)
    }

    private fun getSettings() =
        Settings(
            persistence.getString(nodeTypeKey, ""),
            persistence.getString(nodeIdKey,""),
            persistence.getString(groupIdKey,""),
            persistence.getString(hostIdKey, ""),
            persistence.getString(brokerKey,"")
        );
}


