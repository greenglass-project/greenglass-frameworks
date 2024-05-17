/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.Javalin
import io.javalin.http.Context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch

import io.greenglass.host.application.error.Result
import org.eclipse.jetty.http.HttpStatus

class StringValue(val value:String)
class IntValue(val value:Int)
class DoubleValue(val value:Double)
class BoolValue(val value:Boolean)

data class ErrorResponse(val code : String, val param : String)

class Parameters(val params : Map<String,String>) {

    fun param(name : String) : String =
        checkNotNull(params[name], lazyMessage = { "Parameter $name not found"})
}

class WebService(private val port : Int,
                 private val contextRoot : String,
                 private val backgroundScope : CoroutineScope
) {

    private val subscribeEndpoint = "/subscribe"

    val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    val subsManager = SubscriptionManager(backgroundScope, mapper)

    val logger = KotlinLogging.logger {}

    final val javalin: Javalin = Javalin.create() /*{ config ->
        config.staticFiles.add { staticFiles ->
            staticFiles.hostedPath = "/"
            staticFiles.directory = contextRoot
            staticFiles.location = Location.EXTERNAL
            staticFiles.precompress = false
            staticFiles.aliasCheck = null
        }*/

    init {
        logger.debug { "Starting WebService port = $port root = $contextRoot"}
        javalin.ws(subscribeEndpoint) { ws ->
            ws.onConnect { ctx ->
                logger.info { "Adding client ${ctx.session.hashCode()}" }
                ctx.enableAutomaticPings()
                subsManager.addClient(ctx)
            }
            ws.onMessage { ctx ->
                try {
                    logger.debug {"Received request ${ctx.message()}"}
                    val req = mapper.readValue(ctx.message(),  WebServiceRequest::class.java)
                    logger.info { "Received request for uri=${req.path} unsubscribe=${req.unsubscribe}" }
                    if (req.unsubscribe) {
                        subsManager.unSubscribe(req.path, ctx)
                    } else {
                        subsManager.subscribe(req.path, ctx)
                    }
                } catch(ex:Exception) {
                    logger.error { ex.stackTraceToString() }
                }
            }
            ws.onClose { ctx ->
                logger.info { "Client ${ctx.session.hashCode()} closed reason ${ctx.reason()}" }
                subsManager.removeClient(ctx)
            }
        }
    }

    inline fun <reified T : Any> addProvider(
        uri: String,
        noinline initialValues: ((Map<String,String>) -> List<T>)? = null,
        noinline initialValue: ((Map<String,String>) -> T?)? = null,
        publishFlow: SharedFlow<T>,
        noinline publishParams: ((T) -> Map<String,String>)? = null
    ) {
        logger.debug { "REGISTER PUBLISH on $uri"}
        require(initialValues != null || initialValue != null)
        subsManager.addProvider(uri, T::class, initialValues, initialValue, publishFlow, publishParams)
    }

    inline fun <reified V> addGet(
        path: String,
        coroutineScope: CoroutineScope,
        crossinline getHandler: (Parameters) -> Result<V>
    ) {
        logger.debug { "REGISTER GET on $path"}
        javalin.get(path) { ctx ->
            ctx.future {
                coroutineScope.launch {
                    getHandler(Parameters(ctx.pathParamMap())).route<V>(
                        success = { v: V -> serialiser(ctx,v) },
                        orElse = { c: String, m: String -> errorHandler(ctx,c,m) }
                    )
                }.asCompletableFuture()
            }
        }
    }

    inline fun <reified T, reified V : Any> addPut(
        path: String,
        coroutineScope: CoroutineScope,
        crossinline putHandler: (Parameters,T) -> Result<V>
    ) {
        logger.debug { "REGISTER PUT on $path"}
        javalin.put(path) { ctx ->
            logger.debug { "Received PUT on ${ctx.req().pathInfo} data ${ctx.body()}" }
            val obj: T = mapper.readValue(ctx.body())
            ctx.future {
                coroutineScope.launch {
                    putHandler(Parameters(ctx.pathParamMap()), obj).route<V>(
                        success = { v: V -> serialiser(ctx,v) },
                        orElse = { c: String, m: String -> errorHandler(ctx,c,m) }
                    )
                }.asCompletableFuture()
            }
        }
    }

    inline fun <reified T, reified V> addPost(
        path: String,
        coroutineScope: CoroutineScope,
        crossinline postHandler: (Parameters,T) -> Result<V>
    ) {
        logger.debug { "REGISTER POST on $path"}
        javalin.post(path) { ctx ->
            val obj: T = mapper.readValue(ctx.body())
            ctx.future {
                coroutineScope.launch {
                    postHandler(Parameters(ctx.pathParamMap()), obj).route<V>(
                        success = { v: V -> serialiser(ctx,v)  },
                        orElse = { c: String, m: String -> errorHandler(ctx, c,m) }
                    )
                }.asCompletableFuture()
            }
        }
    }

    fun addDelete(
        path: String,
        coroutineScope: CoroutineScope,
        deleteHandler: (Parameters) -> Result<Unit>
    ) {
        logger.debug { "REGISTER DELETE on $path"}
        javalin.delete(path) { ctx ->
            ctx.future {
                coroutineScope.launch {
                    deleteHandler(Parameters(ctx.pathParamMap())).route<Unit>(
                        success = { ctx.status(HttpStatus.OK_200) },
                        orElse = { c: String, m: String -> errorHandler(ctx,c,m) }
                    )
                }.asCompletableFuture()
            }
        }
    }
    fun serialiser(ctx : Context, value : Any?) {
        if(value == null) {
            ctx.status(HttpStatus.OK_200)
        } else {
            when (value::class) {
                Unit::class -> ctx.status(HttpStatus.OK_200)
                String::class -> ctx.json(mapper.writeValueAsString(StringValue(value as String)))
                Int::class -> ctx.json(mapper.writeValueAsString(IntValue(value as Int)))
                Double::class -> ctx.json(mapper.writeValueAsString(DoubleValue(value as Double)))
                Boolean::class -> ctx.json(mapper.writeValueAsString(BoolValue(value as Boolean)))
                ByteArray::class -> ctx.result(value as ByteArray)
                else -> ctx.json(mapper.writeValueAsString(value))
            }
        }
    }

    fun errorHandler(ctx : Context, code : String, param : String) {
        ctx.status(io.javalin.http.HttpStatus.BAD_REQUEST)
        ctx.json(mapper.writeValueAsString(ErrorResponse(code,param)))
    }

    fun start() {
        logger.debug { "Starting web-service..." }
        javalin.start(port)
    }
}


