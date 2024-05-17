package io.greenglass.host.application.microservice.nats

import kotlinx.coroutines.flow.StateFlow

class EventPublisher<T>(event : String) {
    val flows: HashMap<String, StateFlow<T>> = hashMapOf()



}