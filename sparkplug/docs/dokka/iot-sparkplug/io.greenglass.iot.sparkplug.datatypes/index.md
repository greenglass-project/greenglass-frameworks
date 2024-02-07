//[iot-sparkplug](../../index.md)/[io.greenglass.iot.sparkplug.datatypes](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [Function](-function/index.md) | [jvm]<br>open class [Function](-function/index.md)(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val type: MetricDataType, val direction: [MetricDirection](../io.greenglass.iot.sparkplug.host.models/-metric-direction/index.md), val metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [MetricIdentifier](-metric-identifier/index.md) | [jvm]<br>class [MetricIdentifier](-metric-identifier/index.md)(val nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [MetricNameValue](-metric-name-value/index.md) | [jvm]<br>open class [MetricNameValue](-metric-name-value/index.md)(val metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val value: [MetricValue](-metric-value/index.md)) |
| [MetricValue](-metric-value/index.md) | [jvm]<br>open class [MetricValue](-metric-value/index.md)<br>MetricValue |
| [MwtricValueDeserializer](-mwtric-value-deserializer/index.md) | [jvm]<br>class [MwtricValueDeserializer](-mwtric-value-deserializer/index.md) : StdDeserializer&lt;[MetricValue](-metric-value/index.md)&gt; <br>MwtricValueDeserializer() |
| [NodeMetricNameValue](-node-metric-name-value/index.md) | [jvm]<br>class [NodeMetricNameValue](-node-metric-name-value/index.md)(val nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val value: [MetricValue](-metric-value/index.md)) : [MetricNameValue](-metric-name-value/index.md) |

## Functions

| Name | Summary |
|---|---|
| [MetricDataTypeFromName](-metric-data-type-from-name.md) | [jvm]<br>fun [MetricDataTypeFromName](-metric-data-type-from-name.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): MetricDataType<br>Helper function to convert a MatricDataType name to its enum value |
| [metricValue](metric-value.md) | [jvm]<br>fun Metric.[metricValue](metric-value.md)(): [MetricValue](-metric-value/index.md)<br>Metric.metricValue() |
