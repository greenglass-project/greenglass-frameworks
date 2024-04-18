import com.charleskorn.kaml.Yaml
import io.greenglass.node.core.devicedriver.config.DriverConfig
import io.greenglass.node.core.devicedriver.config.I2cConfig
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class DriverConfigSerialisationTest {

    @Test
    fun serialisaI2c() {
        val i2c:DriverConfig  = I2cConfig (
            name = "ec-sensor",
            device = 1,
            address = 100
        )
        val yaml = Yaml.default.encodeToString(i2c)
        val x=0
    }

    @Test
    fun deserialisaI2c() {

        val yaml = """
                name : "ec-sensor"
                device: 1
                address: 100
        """.trimIndent()

        val obj = Yaml.default
                .decodeFromString(
                    DriverConfig.serializer(),
                    yaml
                )
        assertTrue(obj is I2cConfig)
    }
}