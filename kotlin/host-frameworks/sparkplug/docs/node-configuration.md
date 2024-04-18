# Node Type Configuration

The Sparkplug-Iot module provides a configuration class for Node Types called `NodeType`

> This class is not used directly in the module, rather it is intended to be used in Host applications that use the module.

It does however form the basis of:

- `PhysicalNode` which is used in the Host `SparkplugService`
- `NodeDefinition` which is used in the Node `DriversService`

The class is as follows:

```kotlin
open class NodeType (
    val type : String,
    val name : String,
    val description : String,
    val image : String,
    open val metrics : List<Metric>
) 
```

Where:

- `type` - is a unique name for this node-type
- `name` - is a short display name for the node-type
- `description` - is a description of the functionality provided by this node-type
- `image` - is the name of an image file
- `metrics` - is the list of the metrics supported by this node-type

The `Metric` class is as follows:

```kotlin
open class Metric(
    val name : String,
    val type : MetricDataType,
    val direction : MetricDirection,
    val description : String
)
```

Where:

- `name` - the  metric's name in path format e.g. `/sensors/temperature`
- `type` - the data type of this metric e.g. `MetricDataType.Boolean`
- `directiom` - either `MetricDirection.read` or `MetricDirection.write`
- `description` - a description of the functionaliy provided by this metric
