package io.greenglass.host.control.providers

import kotlinx.coroutines.flow.SharedFlow

interface SequenceStateProvider {
    fun subscribeToSequenceState(instanceId: String, sequenceId: String): SharedFlow<Boolean>
}