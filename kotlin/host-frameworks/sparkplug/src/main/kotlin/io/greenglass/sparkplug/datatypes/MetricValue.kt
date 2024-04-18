/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.sparkplug.datatypes

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.Serializable
import org.eclipse.tahu.message.model.MetaData
import org.eclipse.tahu.message.model.Metric
import org.eclipse.tahu.message.model.MetricDataType
import org.eclipse.tahu.message.model.PropertySet
import java.io.IOException
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

suspend inline fun  Flow<Any>.toMetricValue() : Flow<MetricValue> =
    transform { b -> emit(MetricValue(b)) }

/**
 * Metric.metricValue()
 *
 * Extension function to Tahu Metric class to convert a Metric to a MetricValue
 *
 */
fun Metric.metricValue() = MetricValue(this.dataType, this.value, this.timestamp);


/**
 * Helper function to convert a MatricDataType name to its enum value
 *
 * @param name
 */
fun MetricDataTypeFromName(name : String) =
    MetricDataType.entries.toList().firstOrNull { e -> e.name == name } ?: MetricDataType.Unknown

/**
 * MetricValue
 *
 * A simplified subset of the Tahu Metric class for use in Greenglass
 *
 */


open class MetricValue() {
    /// Tahu metric type
    var type = MetricDataType.Unknown

    /// Actual value as created or required by Tahu
    var value: Any? = null

    /// The timestamp
    var timestamp: Date = Date()


    /**
     * returns true is the metric value is empty
     */
    val isEmpty: Boolean
        @JsonIgnore
        get() = type == MetricDataType.Unknown


    /**
     * returns true if the metric value is not empty
     */
    val isNotEmpty: Boolean
        @JsonIgnore
        get() = type != MetricDataType.Unknown


    /**
     * Returns true is the metric value is a boolean
     */
    val isBoolean: Boolean
        @JsonIgnore
        get() = type == MetricDataType.Boolean && value is Boolean

    /**
     * Returns true is the metric value is a double
     */
    val isDouble: Boolean
        @JsonIgnore
        get() = type == MetricDataType.Double && value is Double


    /**
     * Returns true is the metric value is an Int32
     */
     val isInt32: Boolean
        @JsonIgnore
        get() = type == MetricDataType.Int32 && value is Int


    /**
     * Returns true is the metric value is a Int64
     */
    val isInt64: Boolean
        @JsonIgnore
        get() = type == MetricDataType.Int64 && value is Long

    /**
     * Get the metric value as a 'boolean. Throws IllegalStateException
     * if the value isn't a Boolean
     */
    var boolean: Boolean
        @JsonIgnore
        get() = checkNotNull(value as? Boolean)
        set(value) {
            this.value = value
            this.type = MetricDataType.Boolean
        }

    /**
     * Get the metric value as a Double. Throws IllegalStateException
     * if the value isn't a Double
     */
    var double: Double
        @JsonIgnore
        get() = checkNotNull(value as? Double)
        set(value) {
            this.value = value
            this.type = MetricDataType.Double
        }

    /**
     * Get the metric value as a String. Throws IllegalStateException
     * if the value isn't a string
     */
    var string: String
        @JsonIgnore
        get() = checkNotNull(value as? String)
        set(value) {
            this.value = value
            this.type = MetricDataType.String
        }

    /**
     * Get the metric value as a Short. Throws IllegalStateException
     * if the value isn't a Short
     */
    var int16: Short
        @JsonIgnore
        get() = checkNotNull(value as? Short)
        set(value) {
            this.value = value
            this.type = MetricDataType.Int16
        }

    /**
     * Get the metric value as a Int. Throws IllegalStateException
     * if the value isn't a Int
     */
    var int32: Int
        @JsonIgnore
        get() = checkNotNull(value as? Int)
        set(value) {
            this.value = value
            this.type = MetricDataType.Int32
        }
    /**
     * Get the metric value as a Long. Throws IllegalStateException
     * if the value isn't a Long
     */
    var int64: Long
        @JsonIgnore
        get() = checkNotNull(value as? Long)
        set(value) {
            this.value = value
            this.type = MetricDataType.Int64
        }
    /**
     * Get the metric value as a BigInteger. Throws IllegalStateException
     * if the value isn't a BigInteger
     */
    var uint64: BigInteger
        @JsonIgnore
        get() = checkNotNull(value as? BigInteger)
        set(value) {
            this.value = value
            this.type = MetricDataType.UInt64
        }

    /**
     * Get the metric value as a Float. Throws IllegalStateException
     * if the value isn't a Float
     */
    var float: Float
        @JsonIgnore
        get() = checkNotNull(value as? Float)
        set(value) {
            this.value = value
            this.type = MetricDataType.Float
        }

    /**
     * Get the metric value as a Date. Throws IllegalStateException
     * if the value isn't a Date
     */
    var dateTime: Date
        @JsonIgnore
        get() = checkNotNull(value as? Date)
        set(value) {
            this.value = value
            this.type = MetricDataType.DateTime
        }


    /**
     * Primary constructor
     */
    constructor(type: MetricDataType, value: Any,timestamp: Date = Date()) : this() {
        this.type = type
        this.value = value
        this.timestamp = timestamp 
    }

    /**
     * Construct a double MetricValue from a Kotlin Double
     */
    constructor(value: Double, timestamp: Date = Date()) : this() {
        this.type = MetricDataType.Double
        this.value = value
        this.timestamp = timestamp
    }
    /**
     * Construct a string MetricValue from a Kotlin String
     */
    constructor(value: String,timestamp: Date = Date()) : this() {
        this.type = MetricDataType.String
        this.value = value
        this.timestamp = timestamp
    }

    /**
     * Construct an int16 MetricValue from a Kotlin Int
     */
    constructor(value: Short,timestamp: Date = Date()) : this() {
        this.type = MetricDataType.Int16
        this.value = value
        this.timestamp = timestamp
    }

    /**
     * Construct an long MetricValue from a Kotlin Long
     */
    constructor(value: Long,timestamp: Date = Date()) : this() {
        this.type = MetricDataType.Int64
        this.value = value
        this.timestamp = timestamp
    }

    /**
     * Construct a UInt64 MetricValue from a Kotlin BigInteger
     */
    constructor(value: BigInteger,timestamp: Date = Date()) : this() {
        this.type = MetricDataType.UInt64
        this.value = value
        this.timestamp = timestamp
    }

    /**
     * Construct a float MetricValue from a Kotlin Float
     */
    constructor(value: Float,timestamp: Date = Date()) : this() {
        this.type = MetricDataType.Float
        this.value = value
        this.timestamp = timestamp
    }
    /**
     * Construct a DateTime MetricValue from a Kotlin Date
     */
    constructor(value: Date,timestamp: Date = Date()) : this() {
        this.type = MetricDataType.DateTime
        this.value = value
        this.timestamp = timestamp
    }

    /**
     * Generic constructor from an Any. Checks the actual type of the Any and constructs the
     * corresponding MetricValue.
     */
    constructor(value: Any,timestamp: Date = Date()) : this() {
        this.type = when (value::class) {
            Short::class -> MetricDataType.Int16
            Integer::class -> MetricDataType.Int32
            Long::class -> MetricDataType.Int64
            BigInteger::class -> MetricDataType.UInt64
            Float::class -> MetricDataType.Float
            Double::class -> MetricDataType.Double
            Boolean::class -> MetricDataType.Boolean
            String::class -> MetricDataType.String
            Date::class -> MetricDataType.DateTime
            else -> MetricDataType.Unknown
        }
        this.value = value
        this.timestamp = timestamp
    }

    /**
     * toMetric()
     *
     * Create a Tahu Metric from a Greenglass MetricValue. All the fields of the Tahu Metric not supplied
     * by the MetricValue can be given as parameters, but apart from the metric name (which must be supplied) all
     * other fields are by default empty.
     *
     * @param name metric name
     * @param alias default 0
     * @param isHistorical default false
     * @param isTransient default false
     * @param metaData default empty
     * @param properties default empty
     * @return
     */
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
            this.timestamp,
            this.type,
            isHistorical,
            isTransient,
            metaData,
            properties,
            this.value
        )
    }

    /**
     * toString()
     *
     * Overriden toString() for debugging
     *
     * @return formatted string
     */
    override fun toString(): String = "Type = ${type.name} value = $value timestamp = $timestamp}"

    companion object {
        /**
         * deserialiser()
         *
         * Jackson SimpleModule for deserializing a Metric Value
         *
         * @return SimpleModule
         */
        fun deserialiser() : SimpleModule {
            val module = SimpleModule()
            module.addDeserializer(MetricValue::class.java, MwtricValueDeserializer())
            return module
        }
    }
}

/**
 * MwtricValueDeserializer()
 *
 * MetricValue Jackson deserializer class
 *
 */
class MwtricValueDeserializer : StdDeserializer<MetricValue> {
    constructor() : this(null)
    constructor(vc: Class<*>?) : super(vc)

    /**
     * Jackson deserialise a MetricValue. Uses the type value to set the Any types value
     * field to the correct Kotlin type
     *
     * @param jp
     * @param ctxt
     * @return a MetricValue object
     */
    @ExperimentalStdlibApi
    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): MetricValue {
        val node = jp.codec.readTree<JsonNode>(jp)

        val formatter = SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss");

        val type = MetricDataType.entries.toList().firstOrNull {
            e -> e.name == node["type"].asText()
        } ?: MetricDataType.Unknown

        val value : Any = when(type) {
            MetricDataType.Int16 -> node["value"].shortValue()
            MetricDataType.Int32 -> node["value"].intValue()
            MetricDataType.Int64 -> node["value"].longValue()
            MetricDataType.UInt64 -> node["value"].bigIntegerValue()
            MetricDataType.Double -> node["value"].doubleValue()
            MetricDataType.Boolean -> node["value"].booleanValue()
            MetricDataType.String ->  node["value"].asText()
            MetricDataType.DateTime -> formatter.parse(node["value"].asText())
            else -> Unit
        }

        val timestamp = formatter.parse(node["timestamp"].asText())

        return MetricValue(type, value, timestamp)
    }
}