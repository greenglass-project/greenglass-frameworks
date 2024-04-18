/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.services

import com.charleskorn.kaml.Yaml
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.greenglass.node.core.models.NodeDefinition
import java.io.File

class NodeTypesService(private val configDirectory : String) {
        private val logger = KotlinLogging.logger {}

        fun getType(type: String): NodeDefinition {
            val configFile = File("$configDirectory/$type.yaml")
            logger.debug { "Config file = $configFile"}
            Yaml.default.decodeFromString(Team.serializer(), input)

            val mapper = ObjectMapper(YAMLFactory())
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            mapper.dateFormat = StdDateFormat().withColonInTimeZone(true)
            mapper.registerModules(listOf(KotlinModule.Builder().build()))

            return mapper.readValue(configFile, NodeDefinition::class.java)
        }

    fun types() : List<String> = File(configDirectory)
            .listFiles()
            ?.filter { f -> f.isFile  && f.extension == "yaml"}
            ?.map { fl -> fl.name.split(".").first() }?: listOf()
}
