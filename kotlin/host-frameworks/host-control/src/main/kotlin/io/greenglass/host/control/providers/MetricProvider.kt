package io.greenglass.host.control.providers

import io.greenglass.host.control.sparkplug.datatypes.MetricIdentifier


interface MetricProvider {
    fun metricForVariable(instanceId : String, processId : String, variableId : String) : MetricIdentifier
}