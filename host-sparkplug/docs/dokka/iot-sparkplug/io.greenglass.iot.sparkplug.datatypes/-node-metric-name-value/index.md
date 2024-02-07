//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.datatypes](../index.md)/[NodeMetricNameValue](index.md)

# NodeMetricNameValue

[jvm]\
class [NodeMetricNameValue](index.md)(val nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val value: [MetricValue](../-metric-value/index.md)) : [MetricNameValue](../-metric-name-value/index.md)

## Constructors

| | |
|---|---|
| [NodeMetricNameValue](-node-metric-name-value.md) | [jvm]<br>constructor(nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))constructor(nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [MetricValue](../-metric-value/index.md)) |

## Properties

| Name | Summary |
|---|---|
| [metricName](../-metric-name-value/metric-name.md) | [jvm]<br>val [metricName](../-metric-name-value/metric-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [nodeId](node-id.md) | [jvm]<br>val [nodeId](node-id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](../-metric-name-value/value.md) | [jvm]<br>val [value](../-metric-name-value/value.md): [MetricValue](../-metric-value/index.md) |

## Functions

| Name | Summary |
|---|---|
| [toMetric](../-metric-name-value/to-metric.md) | [jvm]<br>fun [toMetric](../-metric-name-value/to-metric.md)(): Metric |
