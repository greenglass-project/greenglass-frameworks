package io.greenglass.node.core.devicedriver.drivers

import oshi.SystemInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.devicedriver.DriverModule
import io.greenglass.node.core.devicedriver.ReadMetricFunction
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.services.*
import io.greenglass.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetAddress

@Serializable
data class Sysinfo(
    val hardwareMake : String,
    val hardwareModel : String,
    val os :  String,
    val osVersion : String,
    val ipAddress : String,
    val host : String
)

open class SysinfoDriver(name : String,
                         config : List<DriverConfig>,
                         gpio : GpioService,
                         persistence : PersistenceService,
                         webService : WebService,
                         backgroundScope : CoroutineScope
)  : DriverModule(name, config, gpio, persistence, webService, backgroundScope) {

    private val logger = KotlinLogging.logger {}
    private val si = SystemInfo()
    private val sysInfo : Sysinfo

    private lateinit var hardwareMake : ReadMetricFunction
    private lateinit var hardwareModel : ReadMetricFunction
    private lateinit var os : ReadMetricFunction
    private lateinit var osVersion : ReadMetricFunction
    private lateinit var ipAddress : ReadMetricFunction
    private lateinit var host : ReadMetricFunction

    init {
        logger.debug { "Sysinfo driver" }
        val ip = InetAddress.getLocalHost()

        sysInfo = Sysinfo(
            hardwareMake = si.hardware.computerSystem.baseboard.manufacturer,
            hardwareModel = si.hardware.computerSystem.baseboard.model,
            os = si.operatingSystem.family,
            osVersion = si.operatingSystem.versionInfo.version,
            ipAddress = ip.toString(),
            host = ip.hostName
        )
    }

    override suspend fun initialise() {
        hardwareMake = object : ReadMetricFunction(this, "HardwareMake") {}
        hardwareModel = object : ReadMetricFunction(this, "HardwareModel") {}
        os = object : ReadMetricFunction(this, "Os") {}
        osVersion = object : ReadMetricFunction(this, "OsVersion") {}
        ipAddress = object : ReadMetricFunction(this, "IpAddress") {}
        host = object : ReadMetricFunction(this, "Host") {}
    }

    override suspend fun registerSettingsHandler() {
        webService.addGet("/driver/$name/sysinfo") { ctx ->
            val info = Sysinfo(
                hardwareMake = sysInfo.hardwareMake,
                hardwareModel =  sysInfo.hardwareModel,
                os = sysInfo.os,
                osVersion = sysInfo.osVersion,
                ipAddress = sysInfo.ipAddress,
                host = sysInfo.host
            )
            ctx.json(Json.encodeToString(info))
        }
    }

    //InetAddress id = InetAddress.getLocalHost();
    //System.out.println( id.getHostName()

    override suspend fun readAllMetrics() =
        listOf(
            hardwareMake.driverFunctionMetricValue(MetricValue(sysInfo.hardwareMake)),
            hardwareModel.driverFunctionMetricValue(MetricValue(sysInfo.hardwareModel)),
            os.driverFunctionMetricValue(MetricValue(sysInfo.os)),
            osVersion.driverFunctionMetricValue(MetricValue(sysInfo.osVersion)),
            ipAddress.driverFunctionMetricValue(MetricValue(sysInfo.ipAddress)),
            host.driverFunctionMetricValue(MetricValue(sysInfo.host)),
            )

    companion object {
        val type: String = "sysinfo"
    }
}
