package io.greenglass.host.application.influxdb

import com.influxdb.client.write.Point

interface TimeSeriesPoint {
    fun toTimeSeriesPoint() : Point
}