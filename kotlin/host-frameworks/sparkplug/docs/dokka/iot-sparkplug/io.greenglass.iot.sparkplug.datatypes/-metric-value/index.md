//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.datatypes](../index.md)/[MetricValue](index.md)

# MetricValue

[jvm]\
open class [MetricValue](index.md)

MetricValue

A simplified subset of the Tahu Metric class for use in Greenglass

## Constructors

| | |
|---|---|
| [MetricValue](-metric-value.md) | [jvm]<br>constructor(type: MetricDataType, value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Primary constructor<br>constructor(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct a double MetricValue from a Kotlin Double<br>constructor(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct a string MetricValue from a Kotlin String<br>constructor(value: [Short](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-short/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct an int16 MetricValue from a Kotlin Int<br>constructor(value: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct an long MetricValue from a Kotlin Long<br>constructor(value: [BigInteger](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct a UInt64 MetricValue from a Kotlin BigInteger<br>constructor(value: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct a float MetricValue from a Kotlin Float<br>constructor(value: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Construct a DateTime MetricValue from a Kotlin Date<br>constructor(value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), timestamp: [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) = Date())<br>Generic constructor from an Any. Checks the actual type of the Any and constructs the corresponding MetricValue.<br>constructor() |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [boolean](boolean.md) | [jvm]<br>var [boolean](boolean.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Get the metric value as a 'boolean. Throws IllegalStateException if the value isn't a Boolean |
| [dateTime](date-time.md) | [jvm]<br>var [dateTime](date-time.md): [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html)<br>Get the metric value as a Date. Throws IllegalStateException if the value isn't a Date |
| [double](double.md) | [jvm]<br>var [double](double.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>Get the metric value as a Double. Throws IllegalStateException if the value isn't a Double |
| [float](float.md) | [jvm]<br>var [float](float.md): [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Get the metric value as a Float. Throws IllegalStateException if the value isn't a Float |
| [int16](int16.md) | [jvm]<br>var [int16](int16.md): [Short](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-short/index.html)<br>Get the metric value as a Short. Throws IllegalStateException if the value isn't a Short |
| [int32](int32.md) | [jvm]<br>var [int32](int32.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Get the metric value as a Int. Throws IllegalStateException if the value isn't a Int |
| [int64](int64.md) | [jvm]<br>var [int64](int64.md): [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Get the metric value as a Long. Throws IllegalStateException if the value isn't a Long |
| [isBoolean](is-boolean.md) | [jvm]<br>val [isBoolean](is-boolean.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true is the metric value is a boolean |
| [isDouble](is-double.md) | [jvm]<br>val [isDouble](is-double.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true is the metric value is a double |
| [isEmpty](is-empty.md) | [jvm]<br>val [isEmpty](is-empty.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>returns true is the metric value is empty |
| [isInt32](is-int32.md) | [jvm]<br>val [isInt32](is-int32.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true is the metric value is an Int32 |
| [isInt64](is-int64.md) | [jvm]<br>val [isInt64](is-int64.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true is the metric value is a Int64 |
| [isNotEmpty](is-not-empty.md) | [jvm]<br>val [isNotEmpty](is-not-empty.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>returns true if the metric value is not empty |
| [string](string.md) | [jvm]<br>var [string](string.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Get the metric value as a String. Throws IllegalStateException if the value isn't a string |
| [timestamp](timestamp.md) | [jvm]<br>var [timestamp](timestamp.md): [Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html) |
| [type](type.md) | [jvm]<br>var [type](type.md): MetricDataType |
| [uint64](uint64.md) | [jvm]<br>var [uint64](uint64.md): [BigInteger](https://docs.oracle.com/javase/8/docs/api/java/math/BigInteger.html)<br>Get the metric value as a BigInteger. Throws IllegalStateException if the value isn't a BigInteger |
| [value](value.md) | [jvm]<br>var [value](value.md): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [toMetric](to-metric.md) | [jvm]<br>fun [toMetric](to-metric.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), alias: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) = 0, isHistorical: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, isTransient: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, metaData: MetaData = MetaData(), properties: PropertySet = PropertySet()): Metric<br>toMetric() |
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>toString() |
