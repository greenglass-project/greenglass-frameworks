package io.greenglass.host.application.nats.messages

import io.greenglass.host.application.nats.NatsTopic

class NatsRequest<T>(val topic : NatsTopic, val replyTo : String?, val obj : T?)