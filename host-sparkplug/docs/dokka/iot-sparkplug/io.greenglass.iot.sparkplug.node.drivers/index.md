//[iot-sparkplug](../../index.md)/[io.greenglass.iot.sparkplug.node.drivers](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [DriverMetric](-driver-metric/index.md) | [jvm]<br>class [DriverMetric](-driver-metric/index.md)(val function: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val driver: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val value: [MetricValue](../io.greenglass.iot.sparkplug.datatypes/-metric-value/index.md)) |
| [I2cDeviceDriver](-i2c-device-driver/index.md) | [jvm]<br>abstract class [I2cDeviceDriver](-i2c-device-driver/index.md)(val driverName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val bus: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val device: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) |
| [RegisterDriver](-register-driver/index.md) | [jvm]<br>@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html)])<br>annotation class [RegisterDriver](-register-driver/index.md)(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
