

# Core Datatypes

This section describes a set of core datatypes that are used throught the Iot-Sparkplug module, and can be used in applications that use the module.

## MetricValue

In a Sparkplug application values are exchanged as Sparkplug Metrics (which are implemented as Eclipse-Tahu `Metric` class in Greenglass).

Rather than use Metric objects throughout a Greenglass IoT application, the Iot-Sparkplug module defines a simple abstraction containing just the value from a Metric. This is called a `MetricValue` and comprises 3 fields from a Tahu `Metric`

- **type** - the MetricDataType of the value
- **value** - a Kotlin Any? containing the value or null
- **timestamp** - the timestamp

```kotlin
open class MetricValue() {
    var type = MetricDataType.Unknown
    var value: Any? = null
    var timestamp: Date = Date()
}
```

Which can be constructed as follows:

```kotlin
constructor(type: MetricDataType, value: Any,timestamp: Date = Date())
```



### Type-based Functionality

To simply the type-based handling of MetricValues, the class provides additinal functionality for  the following Sparkplug metric types (with their corresponding Kotlin types)

| Sparkplug Data Type     | Kotlin Type |
| ----------------------- | ----------- |
| MetricDataType.Int16    | Short       |
| MetricDataType/Int32    | Integer     |
| MetricDataType.Int64    | Long        |
| MetricDataType.UInt64   | BigInteger  |
| MetricDataType.Float    | Float       |
| MetricDataType.Double   | Double      |
| MetricDataType.Boolean  | Boolean     |
| MetricDataType.String   | String      |
| MetricDataType.DateTime | DateTime    |



The class provides seperate constructors for each os these types

```kotlin
constructor(value: Short,timestamp: Date = Date())
constructor(value: Int,timestamp: Date = Date())
constructor(value: Long,timestamp: Date = Date())
constructor(value: BigInteger,timestamp: Date = Date())
constructor(value: Float, timestamp: Date = Date())
constructor(value: Double, timestamp: Date = Date())
constructor(value: Boolean,timestamp: Date = Date())
constructor(value: String,timestamp: Date = Date())
constructor(value: DateTime,timestamp: Date = Date())
```

The type of the value can be checked per type through a getter function

```kotlin
val isInt16: Boolean
val isInt32: Boolean
val isInt64: Boolean
val isUInt64: Boolean
val isFloat: Boolean
val isDouble: Boolean
val isBoolean: Boolean
val isString: Boolean
val isDateTime: Boolean
```

Getters and setters allow the values to be read and written per type. If actual type of the data doesn't match the getter called an `IllegalStateException` will be thrown.

```kotlin
var int16 : Short
var int32 : Int
var int64 : Long
var uint64 : BigInteger
var float : Float
var double : Double
var boolean : Boolean
var string : String
var dateTime : DateTime
```

e.g for Double

```kotlin
val mv = MetricValue(123.456)

if(mv.isDouble) {
 // do something
}

val value = mv.double

mv.double = 456.124
```



### Conversion to/from Tahu Metric Objects

A `MetricValue` can be converted to and from a Tahu `Metric` object

The `toMetric()` function creates a Metric object using the `type`, `value` and `timestamp` fields. The metric name must be supplied. All other fields of the metric can be supplied but will be defaulted if they are not.

```kotlin
fun toMetric(name: String,
        		 alias: Long = 0L,
        		 isHistorical: Boolean = false,
        	   isTransient: Boolean = false,
         		 metaData: MetaData = MetaData(),
        		 properties: PropertySet = PropertySet(),
 ): Metric
```

In most cases a Metric can be created very simply like this:

```kotlin
val metricValue = MetricValue(123.456)
val metric = metricValue.toMetric("/switches/lights")
```

The IoT-Sparkplug module extends the Tahu Metric class by providing a function to create a MetricValue from a Metric object.

```kotlin
fun Metric.metricValue()
```

For example

```kotlin
val metric = ....
val metricValue = metric.metricValue()
```

## MetricIdentifier

A **MetricIdentifier** references a specific device as a combination of its metricName, and the nodeId of the node that controls the device.

```kotlin
data class MetricIdentifier(val nodeId : String, val metricName : String)
```

## MetricNameValue

A `MetricNameValue` combines a metric name and a `MetricValue`

```kotlin
open class MetricNameValue(val metricName : String, val value : MetricValue)
```

It can be directly converted to a `Metric` with the following function:

```kotlin
fun toMetric() : Metric
```



## NodeMetricNameValue

The `NodeMetricNameValue` class adds a nodeId to a `MetricNameValue`.

```kotlin
class NodeMetricNameValue(val nodeId : String,
                          metricName : String,
                          value : MetricValue
) : MetricNameValue(metricName, value)
```

The class provides a constructor to create a `NodeMetricNameValue` from a `MetricNameValue`

```kotlin
constructor(nodeId : String, metricName : String)
```

