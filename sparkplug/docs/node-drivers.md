# Drivers

## Driver Concepts

The driver's task is to write and/or read  the actual values referenced by individual metrics. It will accomplish this using whatever protocol the corresponding physical device requires.

> Currently only I2c is supported as a device protocol.

The driver has 3 basic operations.

- **Read a value** - all drivers must support this even if the metric is for writing. It is used to get the current value of the metric during the creation of the NBIRTH message. This is an asynchronous function that can support multi-step operations.
- **Write a value** - only for writeable metrics. Write a value to the device.  This is an asynchronous function that can support multi-step operations.
- **Publish a change** - changes to values (both read and write) must be published. The driver provides a SharedFlow for the DriverService to listen for changes. For devices that do not report changes asynchronously the driver can register a repetitive task to read the device periodially and check for a changed value.

A driver had a name that allows it to be referenced from configuration.

A driver can implement multiple 'functions' that can manage aspects of the device and its configuration. Each function will be linked by configuration to a metric.

This is a metric from a `NodeDefinition` file that shows how the `driver` and `function` fields link a metric to its implementation:

```yaml
metrics:
  name: "/switches/lights/value"
  description: "Grow lights control"
  type: "boolean"
  direction: "write"
  driver : "relays"
  function : "relay1-value"
```



## DeviceDriver

`DevivceDriver` is the base class of all Drivers

```kotlin
abstract class DeviceDriver(val driverName : String) {
    val valueFlow = MutableSharedFlow<DriverMetric>(1)
    fun subscribeMetricUpdates(): SharedFlow<DriverMetric> = valueFlow.asSharedFlow()
    abstract suspend fun startAsyncTasks(interval: Int)
    abstract suspend fun stopAsyncTasks()
    abstract suspend fun readValue(function : String) : MetricValue
    abstract suspend fun writeValue(function : String, value: MetricValue)
}
```

Where:

- `valueFlow`  - is the Shared flow for the metric updates

- `subscribeMetricUpdates` - returns the `SharedFlow<DriverMetric>` for this driver

- `startAsyncTasks` - start aynchronous tasks, for example periodically read that value to check for changes. 

- `stopAsyncTasks` - stop aynchronous  tasks

- `readValue` - read the value of the given function

- `writeValue` - write the value to a given function

  

The `DriverMetric` class identifies the source of a metric update and its value.

```kotlin
class DriverMetric(val driver : String,, val function : String,  val value : MetricValue)
```

## Example readValue() and writeValue()

`reacValue()` and `writeValue()` are typically implemented as coroutines. Each  must implement one or more driver functions.

`readValue()` must implement the reading of **all functions** since these are used to construct the NBIRTH

```kotlin
override suspend fun readValue(function : String): MetricValue  = coroutineScope {
    return@coroutineScope when(function) {
    		"value" -> .... // read the value
      	"calibration" -> ... // read the calibration value
        else -> throw UnsupportedOperationException()
		}
}
```



`writeValue()`only implenents the write functions - any any.

```kotlin
override suspend fun writeValue(function : String, value: MetricValue) : Unit = coroutineScope {
   when(function) {
   		"calibration" -> .... // write the calibration value
      else -> throw UnsupportedOperationException()
    }
    valueFlow.emit(DriverMetric(function, driverName, value)
}
```

## Example startAsyncTasks() 

`startAsyncTasks()`  is typically used to poll a device's value to detect changes. 

This can be implemeted using a simple continuous loop with a delay inside a coroutine.

```kotlin
private var currentValue : Double = 0.0
private var job : Job? = null

override suspend fun startAsyncTasks(interval: Int) {
		job = CoroutineScope(Dispatchers.Default).launch {
    		rwhile(true) {
        		val newValue = ....... // read the value from the device
          	if (newValue != currentValue) {
								valueFlow.emit(DriverMetric(value, driverName, MetricValue(newValue)))
              	currentValue = newValue
          	}
          	delay(interval.seconds)
      	}
   	}
}
```

