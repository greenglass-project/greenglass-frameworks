package io.greenglass.host.application.microservice

import kotlinx.coroutines.flow.StateFlow

class EventPublisher<T>(event : String) {
    val flows: HashMap<String, StateFlow<T>> = hashMapOf()



}