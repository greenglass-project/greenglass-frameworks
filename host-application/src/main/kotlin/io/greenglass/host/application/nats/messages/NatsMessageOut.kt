package io.greenglass.host.application.nats.messages


class NatsMessageOut<T>(val topic : String, val obj : T)