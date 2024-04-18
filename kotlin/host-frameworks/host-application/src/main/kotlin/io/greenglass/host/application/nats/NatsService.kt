package io.greenglass.host.application.nats

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.greenglass.host.application.nats.messages.NatsMessageIn
import io.greenglass.host.application.nats.messages.NatsMessageOut
import io.greenglass.host.application.nats.messages.NatsReply
import io.greenglass.host.application.nats.messages.NatsRequest
import io.greenglass.host.application.error.Result
import io.greenglass.host.application.error.Result.Success
import io.greenglass.host.application.error.Result.Failure

import io.nats.client.*
import io.nats.client.impl.Headers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> Flow<NatsReply<T>>.replyToNats(nats: NatsService) =
    collect { r -> nats.publish<T>(r.replyTo, r.result) }


suspend inline fun <reified T> Flow<NatsMessageOut<T>>.publishToNats(nats: NatsService) =
    collect { r -> nats.publish<T>(r.topic, r.obj) }

suspend inline fun <reified T> Flow<T>.publishToNats(nats: NatsService, topic: String) =
    collect { obj -> nats.publish<T>(topic, obj) }


class NatsService(url: String, modules: List<SimpleModule>?) {

    var serviceDispatcher: Dispatcher? = null
    val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private var connection: Connection? = null
    private val logger = KotlinLogging.logger {}

    private val listener = ConnectionListener { conn, type ->
        logger.debug { "NATS event $type" }
        if (type == ConnectionListener.Events.CONNECTED) {
            connection = conn
            serviceDispatcher = connection!!.createDispatcher { msg -> }
        }
    }

    init {
        modules?.forEach { m -> mapper.registerModule(m) }
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        logger.debug { "Starting NatsService..." }
        val options = Options.Builder()
            .server(Options.DEFAULT_URL)
            .connectionListener(listener)
            .server(url)
            .build()

        Nats.connectAsynchronously(options, true);
    }

    /**
     * publishSuccess()
     *
     *  Publish an object as ByteArray with success headers
     *
     * @param topic topic to publish to
     * @param datahe bytearray to be published
     */
    fun publishSuccess(topic: String, data: ByteArray) {
        logger.debug("Publishing Data to $topic")
        val headers = Headers().put("result", "ok")
        connection?.publish(topic, headers, data)
    }

    fun publishFailure(topic: String, errorCode: String, errorMsg: String) {
        //logger.debug("Publishing Data to $topic")
        val headers = Headers()
            .put("result", "failure")
            .put("code", errorCode)
            .put("msg", errorMsg)

        connection?.publish(topic, headers, ByteArray(0))
    }

    /**
     * publishSuccess()
     *
     * Publish an object as JSON with success headers
     *
     * @param topic topic to publish to
     * @param obj the object to be published
     */
    fun <T> publishSuccess(topic: String, obj: T) {
        logger.debug("Publishing to $topic")
        val data = mapper.writeValueAsBytes(obj)
        publishSuccess(topic, data)
    }

    /**
     * publish()
     *
     * Publish a Result object. There are 4 use cases
     * 1. The result is Success but data is Unit - success no payload
     * 2. The result is success and data is ByteArray - publish success withByteArray as payload
     * 3. The result is success and data is an object - publish success with the object as JSON
     * 4. The result is error - publish an error with error code and message
     *
     * @param T
     * @param topic
     * @param result
     */
    inline fun <reified T> publish(topic: String, result: Result<T>) {
        KotlinLogging.logger {}.debug("Publishing to $topic")
        when (result) {
            is Success -> {
                if (T::class == ByteArray::class) {
                    KotlinLogging.logger {}
                        .debug { "Publish success with ByteArray value" }
                    publishSuccess(topic, result.value as ByteArray)
                } else if (T::class != Unit::class) {
                    KotlinLogging.logger {}
                        .debug { "Publish success with value [${mapper.writeValueAsString(result.value)}]" }
                    val data = mapper.writeValueAsBytes(result.value)
                    publishSuccess(topic, data)
                } else {
                    KotlinLogging.logger {}.debug("Publish success no value")
                    publishSuccess(topic, ByteArray(0))
                }
            }

            is Failure -> {
                publishFailure(topic, result.code, result.msg)
            }
        }
    }

    /**
     * publish()
     *
     * Publish a simple object
     *
     * @param T
     * @param topic
     * @param obj
     */
    inline fun <reified T> publish(topic: String, obj: T?) {
        KotlinLogging.logger {}.debug("Publishing to $topic")
        if (obj == null) {
            KotlinLogging.logger {}.debug("Publish success no value")
            publishSuccess(topic, ByteArray(0))
        } else if (T::class == ByteArray::class) {
            KotlinLogging.logger {}.debug { "Publish success with ByteArray value" }
            publishSuccess(topic, obj as ByteArray)
        } else if (T::class != Unit::class) {
            KotlinLogging.logger {}.debug { "Publish success with value [${mapper.writeValueAsString(obj)}]" }
            val data = mapper.writeValueAsBytes(obj)
            publishSuccess(topic, data)
        }
    }

    /**
     * subscribeRequest()
     *
     * @param T
     * @param topic
     * @return
     */
    inline fun <reified T> subscribeRequest(topic: String): SharedFlow<NatsRequest<T>> {
        val logger = KotlinLogging.logger {}
        val flow = MutableSharedFlow<NatsRequest<T>>()
        val natsTopic = NatsTopic(topic)
        logger.debug { "NATS subscribeRequest to ${natsTopic.subscriptionTopic}" }

        val subscription = serviceDispatcher!!.subscribe(natsTopic.subscriptionTopic) { m ->
            logger.debug { "Received message on topic ${m.subject}" }
            natsTopic.setParameters(m.subject)

            val r = m.replyTo

            if (m.data.isNotEmpty()) {
                val o: T = mapper.readValue(m.data)
                runBlocking { flow.emit(NatsRequest(natsTopic, r, o)) }
            } else {
                runBlocking { flow.emit(NatsRequest(natsTopic, r, null)) }
            }
        }
        return flow.asSharedFlow()
    }

    /**
     * subscribeMessageIn
     *
     * @param T
     * @param topic
     * @return
     */
    inline fun <reified T> subscribeMessageIn(topic: String): SharedFlow<NatsMessageIn<T>> {
        val logger = KotlinLogging.logger {}
        val flow = MutableSharedFlow<NatsMessageIn<T>>()
        val natsTopic = NatsTopic(topic)
        logger.debug { "NATS subscribeMessageIn to ${natsTopic.subscriptionTopic}" }

        val subscription = serviceDispatcher!!.subscribe(natsTopic.subscriptionTopic) { m ->
            logger.debug { "Received message on topic ${m.subject}" }
            natsTopic.setParameters(m.subject)
            if (m.data.isNotEmpty()) {
                val o: T = mapper.readValue(m.data)
                runBlocking { flow.emit(NatsMessageIn(natsTopic, o)) }
            } else {
                runBlocking { flow.emit(NatsMessageIn(natsTopic, null)) }
            }
        }
        return flow.asSharedFlow()
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    suspend fun <T> eventSubscriber(event: String, state: StateFlow<T>) {
        val natsScope = CoroutineScope(coroutineContext)
        val dispatcher = newSingleThreadContext("nats")

        serviceDispatcher!!.subscribe(event) { _ -> publishSuccess(event, state.value) }
        natsScope.launch(dispatcher) {
            state.collect { m -> publishSuccess(event, m) }
        }
    }
}
