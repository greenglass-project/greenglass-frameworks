package io.greenglass.host.control.process

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.host.control.datatypes.IdentifiedFlow
import io.greenglass.host.control.datatypes.VariableValue
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ProcessEngineClient(val hostname : String,
                          val port : Int,
                          val secure : Boolean,
                          val coroutineScope : CoroutineScope,
                          val path : String,
                      ) {
    private val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModule(VariableValue.module())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private val logger = KotlinLogging.logger {}

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }

    fun start(inputFlows : List<IdentifiedFlow>) {

        coroutineScope.launch {
            while (true) {
                try {
                    logger.debug { "Connecting to engine on '$hostname:$port"}
                    client.webSocket(
                        method = HttpMethod.Get,
                        host = hostname,
                        port = port,
                        path = "/$path"
                    ) {
                        logger.debug { "Connected " }
                        val input = launch { publishInputFlows(inputFlows) }
                        val output = launch { publishOutputFlows(inputFlows) }

                        output.join()
                        input.cancelAndJoin()
                    }
                    logger.debug { "EXIT" }
                } catch (ex:Exception) {
                    logger.debug { ex.message }
                }

                delay(5000)
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.publishOutputFlows(outputFlows : List<IdentifiedFlow>) {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                println(message.readText())
            }
        } catch (e: Exception) {
            logger.debug { "Error while receiving: ${e.localizedMessage} "}
        }
    }

    private suspend fun DefaultClientWebSocketSession.publishInputFlows(inputFlows : List<IdentifiedFlow>) {
        val crs = inputFlows.map { of ->
            coroutineScope.async {
                of.flow
                    .map {
                        v -> VariableMessage(
                           processId = of.processId,
                            variableId = of.variableId,
                            value = v
                        )
                    }
                    .onEach { e -> logger.debug { "Websocket event ${mapper.writeValueAsString(e)} " } }
                    .collect { vm -> send(mapper.writeValueAsString(vm))}
            }
        }
        crs.awaitAll()
        logger.debug {"INPUTS COMPLETE"}
    }

    private fun StateFlow<VariableValue>.toVariableMessage(p : String, v : String) : Flow<VariableMessage> =
        transform { vv ->  VariableMessage(p,v,vv) }


}


