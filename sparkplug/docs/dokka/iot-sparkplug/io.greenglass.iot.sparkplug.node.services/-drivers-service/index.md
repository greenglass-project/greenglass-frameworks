//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.node.services](../index.md)/[DriversService](index.md)

# DriversService

[jvm]\
class [DriversService](index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |
| [DriverFunction](-driver-function/index.md) | [jvm]<br>data class [DriverFunction](-driver-function/index.md)(val direction: [MetricDirection](../../io.greenglass.iot.sparkplug.host.models/-metric-direction/index.md), val function: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val driver: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [DriverMetricContext](-driver-metric-context/index.md) | [jvm]<br>data class [DriverMetricContext](-driver-metric-context/index.md)(val metricName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val function: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val driver: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [metricForDriverAndFunction](metric-for-driver-and-function.md) | [jvm]<br>fun [metricForDriverAndFunction](metric-for-driver-and-function.md)(driver: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), function: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [readAllMetrics](read-all-metrics.md) | [jvm]<br>suspend fun [readAllMetrics](read-all-metrics.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[MetricNameValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-name-value/index.md)&gt; |
| [registerDriver](register-driver.md) | [jvm]<br>fun [registerDriver](register-driver.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), instance: ([Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) -&gt; [I2cDeviceDriver](../../io.greenglass.iot.sparkplug.node.drivers/-i2c-device-driver/index.md)) |
| [registerMetrics](register-metrics.md) | [jvm]<br>fun [registerMetrics](register-metrics.md)(definition: [NodeDefinition](../../io.greenglass.iot.sparkplug.node.models/-node-definition/index.md)) |
| [startAsyncTasks](start-async-tasks.md) | [jvm]<br>suspend fun [startAsyncTasks](start-async-tasks.md)() |
| [startDriversListener](start-drivers-listener.md) | [jvm]<br>suspend fun [startDriversListener](start-drivers-listener.md)() |
| [stopAsyncTasks](stop-async-tasks.md) | [jvm]<br>suspend fun [stopAsyncTasks](stop-async-tasks.md)() |
| [subscribe](subscribe.md) | [jvm]<br>fun [subscribe](subscribe.md)(): SharedFlow&lt;[MetricNameValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-name-value/index.md)&gt; |
| [writeMetric](write-metric.md) | [jvm]<br>suspend fun [writeMetric](write-metric.md)(metric: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [MetricValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
