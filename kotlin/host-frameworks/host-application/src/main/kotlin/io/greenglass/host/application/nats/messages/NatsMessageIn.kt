package io.greenglass.host.application.nats.messages

import io.greenglass.host.application.nats.NatsTopic

class NatsMessageIn<T>(val topic : NatsTopic, val obj : T?)
