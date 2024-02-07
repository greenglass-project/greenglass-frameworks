package io.greenglass.host.application.nats.messages

import io.greenglass.host.application.error.Result

class NatsReply<T>(val replyTo : String, val result : Result<T>)
