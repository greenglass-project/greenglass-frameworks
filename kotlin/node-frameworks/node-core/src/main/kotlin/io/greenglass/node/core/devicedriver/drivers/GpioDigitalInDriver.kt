package io.greenglass.node.core.devicedriver.drivers

import io.greenglass.node.core.devicedriver.DriverModule

import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.devicedriver.config.GpioDigitalInConfig
import io.greenglass.node.core.services.*
import io.greenglass.node.sparkplug.datatypes.MetricValue
import io.klogging.NoCoLogger
import io.klogging.NoCoLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GpioDigitalInDriver(name : String,
                               config : List<DriverConfig>,
                               gpio : GpioService,
                               persistence : PersistenceService,
                               webService : WebService,
                               backgroundScope : CoroutineScope
) : DriverModule(name,config, gpio, persistence, webService, backgroundScope) , NoCoLogging {

    private val c = checkNotNull(config.firstOrNull() as? GpioDigitalInConfig)
    private val device : GpioService.DigitalInputController

    private val inputState = MutableStateFlow(false)

    private lateinit var state : io.greenglass.node.core.devicedriver.ReadMetricFunction
    var job : Job? = null

    init {
        logger.debug { "Create DigitalInput on pin ${c.pin}"}
        device = gpio.digitalInput(
            pin = c.pin,
            pull = c.pull,
            debounce = c.debounce,
            activeHigh = c.activeHigh
        )
    }

    override suspend fun initialise() = coroutineScope {
        state = object : io.greenglass.node.core.devicedriver.ReadMetricFunction(this@GpioDigitalInDriver, "State") {}

        val ecEventFlow = inputState
            .asSharedFlow()
            .transform { e -> emit(Json.encodeToString(BoolValue(e))) }
            .stateIn(backgroundScope)
        webService.addEventPublisher("/driver/$name/state", ecEventFlow)
    }

    override suspend fun readAllMetrics(): List<io.greenglass.node.core.devicedriver.DriverFunctionMetricValue> =
        listOf(state.driverFunctionMetricValue(MetricValue(device.read())))

    override fun startUpdates() {
        logger.debug { "GpioDigitalInDriver.startUpdates()"}
        job = backgroundScope.launch {
            device
                .subscribe()
                .collect { s ->
                    valueFlow.emit(listOf(state.driverFunctionMetricValue(MetricValue(s))))
                    inputState.emit(s)
                }
        }
    }

    override fun stopUpdates() {
        job?.cancel()
        device.unSubscribe()    }



    companion object {
        val type: String = "gpio_digital_in"
    }
}