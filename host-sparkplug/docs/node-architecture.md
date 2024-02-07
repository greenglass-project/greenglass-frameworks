# Node Architecture



The IoT-Sparkplug modules contains a set of components that can be used to create Kotlin Sparkplug Node implementations.

The archuitecture of such an implementation looks like this.

![](images/node-architecture.png)

The module contains 3 service components which are the principal building blocks af Node implementation

- `SparkplugService` - handles the MQ/TT connection and implements the Sparkplug protocol. 
- `DriverService` - provides a bridge between the Sparkplug metrics and the device drivers. The DriverService contains no drivers, but allows drivers to be "plugged-in", and configured such that metrics are mapped onto driver instances and drivers functions. This mapping is defined in `NodeDefinion`configuration file. The Driver concept is described in detail in xxxxxxxx.
- `PersistenceService` - is a service that is available to drivers to persist driver specific values.

> Note the module does not provide a complete Node implementation since it has no drivers. The Node implementation is created at application level with a set of drivers appropriate to the application.