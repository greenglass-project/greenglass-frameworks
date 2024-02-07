//[iot-sparkplug](../../index.md)/[io.greenglass.iot.sparkplug.node.models](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [Driver](-driver/index.md) | [jvm]<br>class [Driver](-driver/index.md)(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val i2c: [I2c](-i2c/index.md), val functions: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Function](../io.greenglass.iot.sparkplug.datatypes/-function/index.md)&gt;) |
| [I2c](-i2c/index.md) | [jvm]<br>open class [I2c](-i2c/index.md)(val device: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val address: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) |
| [NodeDefinition](-node-definition/index.md) | [jvm]<br>class [NodeDefinition](-node-definition/index.md)(val type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val image: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val drivers: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Driver](-driver/index.md)&gt;) |
