package io.greenglass.host.control.sparkplug.datatypes

open class MetricNameValue(
    val metricName : String,
    val value : MetricValue
) {
    fun toMetric() = value.toMetric(metricName)
}