package io.greenglass.host.control.providers

import io.greenglass.host.control.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.flow.SharedFlow

interface SetpointProvider {
    fun subscribeToSetpoint(instanceId : String, variableId : String) : SharedFlow<MetricValue>
}