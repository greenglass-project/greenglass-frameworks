//[iot-sparkplug](../../../index.md)/[io.greenglass.iot.sparkplug.datatypes](../index.md)/[MwtricValueDeserializer](index.md)

# MwtricValueDeserializer

[jvm]\
class [MwtricValueDeserializer](index.md) : StdDeserializer&lt;[MetricValue](../-metric-value/index.md)&gt; 

MwtricValueDeserializer()

MetricValue Jackson deserializer class

## Constructors

| | |
|---|---|
| [MwtricValueDeserializer](-mwtric-value-deserializer.md) | [jvm]<br>constructor()constructor(vc: [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)&lt;*&gt;?) |

## Functions

| Name | Summary |
|---|---|
| [deserialize](index.md#-1550234985%2FFunctions%2F-1216412040) | [jvm]<br>open fun [deserialize](index.md#-1550234985%2FFunctions%2F-1216412040)(p0: JsonParser, p1: DeserializationContext, p2: [MetricValue](../-metric-value/index.md)): [MetricValue](../-metric-value/index.md)<br>[jvm]<br>open override fun [deserialize](deserialize.md)(jp: JsonParser, ctxt: DeserializationContext?): [MetricValue](../-metric-value/index.md)<br>Jackson deserialise a MetricValue. Uses the type value to set the Any types value field to the correct Kotlin type |
| [deserializeWithType](index.md#-1774505680%2FFunctions%2F-1216412040) | [jvm]<br>open fun [deserializeWithType](index.md#-1774505680%2FFunctions%2F-1216412040)(p0: JsonParser, p1: DeserializationContext, p2: TypeDeserializer, p3: [MetricValue](../-metric-value/index.md)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)<br>open override fun [deserializeWithType](index.md#-839578978%2FFunctions%2F-1216412040)(p0: JsonParser, p1: DeserializationContext, p2: TypeDeserializer): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [findBackReference](index.md#1438700766%2FFunctions%2F-1216412040) | [jvm]<br>open fun [findBackReference](index.md#1438700766%2FFunctions%2F-1216412040)(p0: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): SettableBeanProperty |
| [getAbsentValue](index.md#-390729380%2FFunctions%2F-1216412040) | [jvm]<br>open override fun [getAbsentValue](index.md#-390729380%2FFunctions%2F-1216412040)(p0: DeserializationContext): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [getDelegatee](index.md#-1050556161%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getDelegatee](index.md#-1050556161%2FFunctions%2F-1216412040)(): JsonDeserializer&lt;*&gt; |
| [getEmptyAccessPattern](index.md#2004370652%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getEmptyAccessPattern](index.md#2004370652%2FFunctions%2F-1216412040)(): AccessPattern |
| [getEmptyValue](index.md#2066120599%2FFunctions%2F-1216412040) | [jvm]<br>open fun [~~getEmptyValue~~](index.md#2066120599%2FFunctions%2F-1216412040)(): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)<br>open fun [getEmptyValue](index.md#-1621668596%2FFunctions%2F-1216412040)(p0: DeserializationContext): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [getKnownPropertyNames](index.md#808020811%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getKnownPropertyNames](index.md#808020811%2FFunctions%2F-1216412040)(): [MutableCollection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-collection/index.html)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; |
| [getNullAccessPattern](index.md#-96796966%2FFunctions%2F-1216412040) | [jvm]<br>open override fun [getNullAccessPattern](index.md#-96796966%2FFunctions%2F-1216412040)(): AccessPattern |
| [getNullValue](index.md#-1752557675%2FFunctions%2F-1216412040) | [jvm]<br>open fun [~~getNullValue~~](index.md#-1752557675%2FFunctions%2F-1216412040)(): [MetricValue](../-metric-value/index.md)<br>open override fun [getNullValue](index.md#432781262%2FFunctions%2F-1216412040)(p0: DeserializationContext): [MetricValue](../-metric-value/index.md) |
| [getObjectIdReader](index.md#911426750%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getObjectIdReader](index.md#911426750%2FFunctions%2F-1216412040)(): ObjectIdReader |
| [getValueClass](index.md#255254003%2FFunctions%2F-1216412040) | [jvm]<br>fun [~~getValueClass~~](index.md#255254003%2FFunctions%2F-1216412040)(): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)&lt;*&gt; |
| [getValueInstantiator](index.md#1591006481%2FFunctions%2F-1216412040) | [jvm]<br>open override fun [getValueInstantiator](index.md#1591006481%2FFunctions%2F-1216412040)(): ValueInstantiator |
| [getValueType](index.md#943959893%2FFunctions%2F-1216412040) | [jvm]<br>open fun [getValueType](index.md#943959893%2FFunctions%2F-1216412040)(): JavaType<br>open fun [getValueType](index.md#-956205042%2FFunctions%2F-1216412040)(p0: DeserializationContext): JavaType |
| [handledType](index.md#2000742074%2FFunctions%2F-1216412040) | [jvm]<br>open override fun [handledType](index.md#2000742074%2FFunctions%2F-1216412040)(): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)&lt;*&gt; |
| [isCachable](index.md#1654902530%2FFunctions%2F-1216412040) | [jvm]<br>open fun [isCachable](index.md#1654902530%2FFunctions%2F-1216412040)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [logicalType](index.md#1638353390%2FFunctions%2F-1216412040) | [jvm]<br>open fun [logicalType](index.md#1638353390%2FFunctions%2F-1216412040)(): LogicalType |
| [replaceDelegatee](index.md#79105129%2FFunctions%2F-1216412040) | [jvm]<br>open fun [replaceDelegatee](index.md#79105129%2FFunctions%2F-1216412040)(p0: JsonDeserializer&lt;*&gt;): JsonDeserializer&lt;*&gt; |
| [supportsUpdate](index.md#336340330%2FFunctions%2F-1216412040) | [jvm]<br>open fun [supportsUpdate](index.md#336340330%2FFunctions%2F-1216412040)(p0: DeserializationConfig): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [unwrappingDeserializer](index.md#-1815728544%2FFunctions%2F-1216412040) | [jvm]<br>open fun [unwrappingDeserializer](index.md#-1815728544%2FFunctions%2F-1216412040)(p0: NameTransformer): JsonDeserializer&lt;[MetricValue](../-metric-value/index.md)&gt; |
