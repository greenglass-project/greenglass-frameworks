package io.greenglass.host.control.datatypes

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.greenglass.host.control.datatypes.VariableValueType.*
import io.greenglass.host.control.sparkplug.datatypes.MetricValue
import kotlinx.coroutines.flow.MutableStateFlow
import org.eclipse.tahu.message.model.MetaData
import org.eclipse.tahu.message.model.Metric
import org.eclipse.tahu.message.model.PropertySet
import java.io.IOException
import java.math.BigInteger
import java.util.*

fun valueOfType(type : VariableValueType, value : Any?) : VariableValue {
    return when(type) {
        Int16Type -> VariableValue(Int16Type, nullOrType<Short>(value))
        Int32Type -> VariableValue(Int32Type, nullOrType<Int>(value))
        Int64Type -> VariableValue(Int64Type, nullOrType<Long>(value))
        Uint64Type -> VariableValue(Uint64Type, nullOrType<BigInteger>(value))
        FloatType -> VariableValue(FloatType, nullOrType<Float>(value))
        DoubleType -> VariableValue(DoubleType, nullOrType<Double>(value))
        BooleanType -> VariableValue(BooleanType, nullOrType<Boolean>(value) )
        StringType -> VariableValue(StringType, nullOrType<String>(value))
        DatetimeType -> VariableValue(DatetimeType, nullOrType<Date>(value))
        UnknownType -> VariableValue(UnknownType, null)
    }
}
private inline fun  <reified T : Any> nullOrType(value : Any?) : T? =
    if(value == null) null else checkNotNull(value as? T)

class VariableValue(val type : VariableValueType,
                    val value : Any? = null
) {
    fun toMetric(
        name: String,
        alias: Long = 0L,
        isHistorical: Boolean = false,
        isTransient: Boolean = false,
        metaData: MetaData = MetaData(),
        properties: PropertySet = PropertySet(),
    ): Metric {
        return Metric(
            name,
            alias,
            Date(),
            this.type.tahuType,
            isHistorical,
            isTransient,
            metaData,
            properties,
            this.value
        )
    }

    fun toMetricValue() = MetricValue(type.tahuType, value)

    companion object {
        /**
         * Helper function to create the module
         * for Jackson deserialisation
         *
         * @return
         */
        fun module(): SimpleModule =
            SimpleModule("VariableValue")
                .addDeserializer(VariableValue::class.java, VariableValueDeserializer())


        fun fromMetricValue(metricValue: MetricValue) =
            VariableValue(
                type = VariableValueType.fromMetricDataType(metricValue.type),
                value = metricValue.value
            )
    }
}

/**
 * VariableValueDeserializer
 *
 * Custom deserialiser for VariableValue
 * returns the calue as the correct type
 */
class VariableValueDeserializer : StdDeserializer<VariableValue> {
    constructor() : this(null)
    constructor(vc: Class<*>?) : super(vc)

    @ExperimentalStdlibApi
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): VariableValue {
        val node = jp.codec.readTree<JsonNode>(jp)

        val type = VariableValueType.valueOf(node["type"].asText())
        return valueOfType(
            type = type,
            value = node["value"]
        )
    }
}
