/******************************************************************************
 *  Copyright 2024 Greenglass Project
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.influxdb

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import kotlinx.coroutines.flow.filter


fun Point.addAnyField(field : String, value : Any) : Point {
        when(value) {
            is Double -> this.addField(field, value )
            is Int -> this.addField(field, value)
            is Long -> this.addField(field, value)
            is Boolean -> this.addField(field, value)
            is String -> this.addField(field, value)
            else -> this.addField(field, "Unsupported type ${value::class.java.simpleName}")
        }
    return this
}

/*suspend inline fun  Flow<NodeMetricNameValue>.toTimeLine(client: InfluxDbService) =
    filter { e -> e.value.value != null }
        .map { m -> Point
                .measurement(m.metricName)
                .addTag("nodeId", m.nodeId)
                .addAnyField("value", m.value.value!!)
                .time(m.value.timestamp.toInstant(), WritePrecision.MS)
        }
        .collect { p -> client
            .client
            .getWriteKotlinApi()
            .writePoint(p)
        }
*/






class TimelineFlows {
}