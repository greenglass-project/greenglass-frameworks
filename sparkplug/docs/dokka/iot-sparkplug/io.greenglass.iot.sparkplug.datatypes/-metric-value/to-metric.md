//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.datatypes](../index.md)/[MetricValue](index.md)/[toMetric](to-metric.md)

# toMetric

[jvm]\
fun [toMetric](to-metric.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), alias: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) = 0, isHistorical: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, isTransient: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, metaData: MetaData = MetaData(), properties: PropertySet = PropertySet()): Metric

toMetric()

Create a Tahu Metric from a Greenglass MetricValue. All the fields of the Tahu Metric not supplied by the MetricValue can be given as parameters, but apart from the metric name (which must be supplied) all other fields are by default empty.

#### Return

#### Parameters

jvm

| | |
|---|---|
| name | metric name |
| alias | default 0 |
| isHistorical | default false |
| isTransient | default false |
| metaData | default empty |
| properties | default empty |
