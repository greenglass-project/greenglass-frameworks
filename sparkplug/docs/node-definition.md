# Node Definition

The `NodeDefinition` is a YAML configuration file that defines the metrics that this node implements, and 

```yaml
type : "LIGHTS_CONTROLLER"
name : "Lights controller"
description : "On-off lights controller"
image : ""

metrics:
  name: "/switches/lights/value"
  description: "Grow lights control"
  type: "boolean"
  direction: "write"
  driver : "relays"
  function : "relay1-value"

drivers:
  - name : "relays"
    type: "m5_u097_4_relays"

    i2c:
      device: 1
      address: 38

```



```kotlin
class NodeDefinition (
    type : String,
    name : String,
    description : String,
    image : String,
    override val metrics : List<MetricDefinition>,
    val drivers : List<Driver>
) : NodeType(type, name, description, image, metrics)

```



```kotlin
class MetricDefinition(name : String,
                       type : MetricDataType,
                       direction : MetricDirection,
                       description : String,
                       val driver : String,
                       val function : String
) : Metric(name, type, direction, description )
```

