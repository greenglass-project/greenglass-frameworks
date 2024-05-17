package io.greenglass.host.application.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.host.application.components.treewalker.TreeWalker
import io.javalin.websocket.WsContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.IllegalStateException
import kotlin.reflect.KClass


class SubscriptionManager(private val backgroundScope : CoroutineScope, private val mapper: ObjectMapper
) {
    private val logger = KotlinLogging.logger {}

    private val clients: HashMap<Int, ClientContext> = hashMapOf() // sessionId
    //private val providers: HashMap<String, ProviderContext> = hashMapOf()

    private val providers = TreeWalker<ProviderContext>()

    data class ClientContext(val context: WsContext, val subscriptions: ArrayList<String>)
    data class ProviderContext(
        val uri: String,
        val type: KClass<*>,
        val flow: SharedFlow<*>,
        val initialValues: ((Map<String,String>) -> List<*>)?,
        val initialValue: ((Map<String,String>) -> Any?)?,
        val subscribers: HashMap<String,ArrayList<ClientContext>>
    )

    @Throws(IllegalStateException::class)
    fun <T> addProvider(uri: String,
                    type: KClass<*>,
                    initialValues: ((Map<String,String>) -> List<T>)?,
                    initialValue: ((Map<String,String>) -> T?)?,
                    publishFlow: SharedFlow<T>,
                    publishParams: ((T) -> Map<String,String>)?
    ) {

        //logger.debug { "PUBLISH on $uri"}
        require(initialValues != null || initialValue != null)
        this.providers.add(uri, ProviderContext(uri, type, publishFlow, initialValues, initialValue, hashMapOf()))

        if (publishParams != null) {
            backgroundScope.launch {
                publishFlow.map { o : T -> WebserviceEvent(
                    path = providers.build(uri,publishParams(o)),
                    data = o
                )}.map { e -> Pair(mapper.writeValueAsString(e), e.path) }
                    .onEach { f -> logger.debug { "Event = ${f.first}"}}
                    .collect { g ->
                        providers.match(g.second)?.type?.subscribers?.get(g.second)?.forEach { s ->
                            try { s.context.send(g.first) } catch (_:IOException) {}
                        }
                }
            }
        } else {
            backgroundScope.launch {
                publishFlow.map { o : T -> WebserviceEvent(
                    path = uri,
                    data = o
                )}
                    .map { e -> Pair(mapper.writeValueAsString(e), e.path) }
                    .onEach { f -> logger.debug { "Webservice Event = ${f.first}"}}
                    .collect { g ->
                    providers.match(g.second)?.type?.subscribers?.get(g.second)?.forEach { s ->
                        try { s.context.send(g.first) } catch (_:IOException) {}
                    }
                }
            }
        }
    }

    fun addClient(context : WsContext) {
        logger.debug { "SubscriptionManager - adding client ${context.session.hashCode()}"}
        clients[context.session.hashCode()] = ClientContext(context, arrayListOf())
    }

    fun removeClient(context: WsContext) {
        val clientCtx = clients[context.session.hashCode()]
        if (clientCtx != null) {
            logger.debug { "SubscriptionManager - removing client ${context.session.hashCode()}"}
            clientCtx.subscriptions.forEach { s ->
                providers.match(s)?.type?.subscribers?.get(s)?.remove(clientCtx)
            }
            clients.remove(context.session.hashCode())
        }
    }

    fun subscribe(path: String, context: WsContext) {
        logger.debug { "Subscribe request from client ${context.session.hashCode()}" }
        // Get the provider from the tree
        val result = checkNotNull(providers.match(path), lazyMessage = { "No Provider for $path" })
        val provCtx = result.type
        val params = result.params.toMap()

        // Check that this client is connected
        val clientCtx = clients[context.session.hashCode()]
        if (clientCtx != null) {
            val clients = provCtx.subscribers.getOrPut(path) { arrayListOf() }
            if(!clients.contains(clientCtx)) {
                clients.add(clientCtx)

                // Fetch and send the initial values
                // only one of these will be called

                provCtx.initialValues?.let { f ->
                    f(params).forEach { v ->
                        val json = mapper.writeValueAsString(WebserviceEvent(path,v))
                        logger.debug { "Initial value : $json"}
                        try { clientCtx.context.send(json) } catch (_:IOException) {}
                    }
                }
                provCtx.initialValue
                    ?.let { f -> f(params) }
                    .let { v ->
                        if (v != null) {
                            val json = mapper.writeValueAsString(WebserviceEvent(path, v))
                            logger.debug { "Initial value : $json" }
                            try { clientCtx.context.send(json) } catch (_:IOException) {}
                        }
                    }
            }
        }
    }

    fun unSubscribe(uri: String, context: WsContext) {
        val provCtx = checkNotNull(providers.match(uri), lazyMessage = { "No Provider for $uri" }).type
        val clientCtx = clients[context.session.hashCode()]
        if (clientCtx != null) {
            provCtx.subscribers[uri]?.remove(clientCtx)
            clientCtx.subscriptions.remove(uri)
        }
    }
}