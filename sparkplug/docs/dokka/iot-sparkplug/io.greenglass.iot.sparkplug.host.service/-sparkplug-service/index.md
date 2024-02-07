//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.host.service](../index.md)/[SparkplugService](index.md)

# SparkplugService

[jvm]\
class [SparkplugService](index.md)(uri: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), groupId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), hostId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))

## Constructors

| | |
|---|---|
| [SparkplugService](-sparkplug-service.md) | [jvm]<br>constructor(uri: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), groupId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), hostId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [addNodeDefinition](add-node-definition.md) | [jvm]<br>fun [addNodeDefinition](add-node-definition.md)(nd: [PhysicalEonNode](../../io.greenglass.iot.sparkplug.host.models/-physical-eon-node/index.md)): StateFlow&lt;[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)&gt;<br>addNodeDefinition |
| [publishToMetric](publish-to-metric.md) | [jvm]<br>fun [publishToMetric](publish-to-metric.md)(nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [MetricValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md)) |
| [removeNode](remove-node.md) | [jvm]<br>fun [removeNode](remove-node.md)(nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [subscribeToMetric](subscribe-to-metric.md) | [jvm]<br>fun [subscribeToMetric](subscribe-to-metric.md)(metric: [MetricIdentifier](../../io.greenglass.iot.sparkplug.datatypes/-metric-identifier/index.md)): StateFlow&lt;[MetricValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md)&gt;<br>fun [subscribeToMetric](subscribe-to-metric.md)(nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): StateFlow&lt;[MetricValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md)&gt; |
| [subscribeToNodeState](subscribe-to-node-state.md) | [jvm]<br>fun [subscribeToNodeState](subscribe-to-node-state.md)(nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): StateFlow&lt;[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)&gt; |
