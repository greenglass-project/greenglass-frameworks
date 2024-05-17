package io.greenglass.host.application.microservice.nats

import io.greenglass.host.application.nats.NatsService
import kotlinx.coroutines.flow.*
import io.github.oshai.kotlinlogging.KotlinLogging

import io.greenglass.host.application.error.Result
import io.greenglass.host.application.nats.messages.NatsMessageOut
import io.greenglass.host.application.nats.messages.NatsReply
import io.greenglass.host.application.nats.messages.NatsRequest
import io.greenglass.host.application.nats.replyToNats

val logger = KotlinLogging.logger {}

/**
 * .handleRequest()
 *
 * Microservice function to take a request and return a response
 *
 * @param T type pf the request payload. Unit if to payload
 * @param V type of the response payload. Unit if noine
 * @param service the service function to be called
 * @return Flow of NatsReply messages
 */
suspend inline fun <reified T, V : Any> Flow<NatsRequest<T>>.handleRequest(crossinline service : (NatsRequest<T>)-> Result<V>) : Flow<NatsReply<V>> =
    transform { r -> emit(NatsReply(r.replyTo!!, service(r))) }

/**
 * microService()
 *
 * A microservice. Listen to a nats topic. Handle the request and publish the response as the response.
 * @param T type pf the request payload. Unit if to payload
 * @param V type of the response payload. Unit if noine
 * @param nats nats client instance
 * @param topic request/response topic
 * @param service micro-service function to be called
 */
suspend inline fun <reified T, reified V : Any>microService(nats : NatsService,
                                                            topic : String,
                                                            crossinline service : (NatsRequest<T>) -> Result<V>
)  {
    nats.subscribeRequest<T>(topic)
        .handleRequest { r -> service(r) }
        .replyToNats<V>(nats)
}


suspend inline fun <reified T>Flow<NatsMessageOut<T>>.publishEvent(nats : NatsService) =
    collect { r -> nats.publish<T>(r.topic, r.obj) }

suspend inline fun <reified T>Flow<T>.publishEvent(nats : NatsService, topic : String) =
    collect { r -> nats.publish<T>(topic, r) }


