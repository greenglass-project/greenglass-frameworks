/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.control.datatypes

import org.eclipse.tahu.message.model.MetricDataType

enum class VariableValueType(val value : String, val tahuType : MetricDataType) {
    Int16Type("int16", MetricDataType.Int16),
    Int32Type("int32", MetricDataType.Int32),
    Int64Type("int64", MetricDataType.Int64),
    Uint64Type("uint64", MetricDataType.UInt64),
    FloatType("float", MetricDataType.Float),
    DoubleType("double", MetricDataType.Double ),
    BooleanType("boolean", MetricDataType.Boolean),
    StringType("string", MetricDataType.String),
    DatetimeType("datetime", MetricDataType.DateTime),
    UnknownType("unknown", MetricDataType.Unknown);

    companion object {
        var lookup: Map<MetricDataType, VariableValueType>? = null;

        fun fromMetricDataType(type: MetricDataType): VariableValueType {
            if (lookup == null)
                lookup = entries.associateBy { e -> e.tahuType }
            return checkNotNull(
                lookup?.get(type),
                lazyMessage =  { "VariableValueType - MetricDataType ${type.name} not found"}
            )
        }
    }
}
