package io.greenglass.host.control.controlprocess.providers

import kotlinx.coroutines.flow.SharedFlow

interface ProcessStateProvider {
    fun subscribeToProcessState(instanceId: String, processId: String): SharedFlow<Boolean>
}
