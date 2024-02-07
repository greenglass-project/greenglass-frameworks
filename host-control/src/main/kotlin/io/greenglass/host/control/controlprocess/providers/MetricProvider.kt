package io.greenglass.host.control.controlprocess.providers

import io.greenglass.sparkplug.datatypes.MetricIdentifier


interface MetricProvider {
    fun metricForVariable(instanceId : String, processId : String, variableId : String) : MetricIdentifier
}