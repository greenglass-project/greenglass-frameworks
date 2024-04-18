package io.greenglass.host.control.controlprocess.providers

import io.greenglass.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.flow.SharedFlow

interface SetpointProvider {
    fun subscribeToSetpoint(instanceId : String, variableId : String) : SharedFlow<MetricValue>
}