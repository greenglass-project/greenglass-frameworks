//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.node.drivers](../index.md)/[I2cDeviceDriver](index.md)

# I2cDeviceDriver

[jvm]\
abstract class [I2cDeviceDriver](index.md)(val driverName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val bus: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val device: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))

## Constructors

| | |
|---|---|
| [I2cDeviceDriver](-i2c-device-driver.md) | [jvm]<br>constructor(driverName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), bus: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), device: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [bus](bus.md) | [jvm]<br>val [bus](bus.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [device](device.md) | [jvm]<br>val [device](device.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [driverName](driver-name.md) | [jvm]<br>val [driverName](driver-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [readValue](read-value.md) | [jvm]<br>abstract suspend fun [readValue](read-value.md)(function: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [MetricValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md) |
| [startAsyncTasks](start-async-tasks.md) | [jvm]<br>abstract suspend fun [startAsyncTasks](start-async-tasks.md)(interval: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) |
| [stopAsyncTasks](stop-async-tasks.md) | [jvm]<br>abstract suspend fun [stopAsyncTasks](stop-async-tasks.md)() |
| [subscribeMetricUpdates](subscribe-metric-updates.md) | [jvm]<br>abstract fun [subscribeMetricUpdates](subscribe-metric-updates.md)(): SharedFlow&lt;[DriverMetric](../-driver-metric/index.md)&gt; |
| [writeValue](write-value.md) | [jvm]<br>abstract suspend fun [writeValue](write-value.md)(function: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [MetricValue](../../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md)) |
