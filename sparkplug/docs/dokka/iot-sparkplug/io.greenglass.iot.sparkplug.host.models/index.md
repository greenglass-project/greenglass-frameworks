//[iot-sparkplug](../../index.md)/[io.greenglass.iot.sparkplug.host.models](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [EonNode](-eon-node/index.md) | [jvm]<br>open class [EonNode](-eon-node/index.md)(val type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val image: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val metrics: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Metric](-metric/index.md)&gt;) |
| [Metric](-metric/index.md) | [jvm]<br>class [Metric](-metric/index.md)(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val type: MetricDataType, val direction: [MetricDirection](-metric-direction/index.md), metric: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Function](../io.greenglass.iot.sparkplug.datatypes/-function/index.md) |
| [MetricDirection](-metric-direction/index.md) | [jvm]<br>enum [MetricDirection](-metric-direction/index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[MetricDirection](-metric-direction/index.md)&gt; |
| [PhysicalEonNode](-physical-eon-node/index.md) | [jvm]<br>class [PhysicalEonNode](-physical-eon-node/index.md)(val nodeId: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val image: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val metrics: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Metric](-metric/index.md)&gt;) : [EonNode](-eon-node/index.md) |
