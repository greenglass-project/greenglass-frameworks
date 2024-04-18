//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.datatypes](../index.md)/[MetricValue](index.md)/[MetricValue](-metric-value.md)

# MetricValue

[jvm]\
constructor(type: MetricDataType, value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Primary constructor

[jvm]\
constructor(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct a double MetricValue from a Kotlin Double

[jvm]\
constructor(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct a string MetricValue from a Kotlin String

[jvm]\
constructor(value: [Short](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-short/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct an int16 MetricValue from a Kotlin Int

[jvm]\
constructor(value: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct an long MetricValue from a Kotlin Long

[jvm]\
constructor(value: [BigInteger](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct a UInt64 MetricValue from a Kotlin BigInteger

[jvm]\
constructor(value: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct a float MetricValue from a Kotlin Float

[jvm]\
constructor(value: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Construct a DateTime MetricValue from a Kotlin Date

[jvm]\
constructor(value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())

Generic constructor from an Any. Checks the actual type of the Any and constructs the corresponding MetricValue.

[jvm]\
constructor()
