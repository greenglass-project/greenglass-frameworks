/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.host.application.influxdb

import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import io.github.oshai.kotlinlogging.KotlinLogging

class InfluxDbService(url : String,
                      val org : String,
                      val bucket : String,
                      token : String
) {
    private val logger = KotlinLogging.logger {}
    val client = InfluxDBClientKotlinFactory.create(url, token.toCharArray(), org, bucket)

    val writeApi = client.getWriteKotlinApi()
    val queryApi = client.getQueryKotlinApi()

    init {
        logger.debug { "Starting InfluxDbService..." }
    }
}