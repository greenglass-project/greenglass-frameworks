/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.configuration

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.greenglass.node.core.models.NodeDefinition

class ConfigLoader {
    companion object {
        fun load(type: String): NodeDefinition {

            val configUrl = ConfigLoader::class.java.classLoader.getResource("nodes/$type.yaml")

            val mapper = ObjectMapper(YAMLFactory())
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            mapper.dateFormat = StdDateFormat().withColonInTimeZone(true)
            mapper.registerModules(listOf(KotlinModule.Builder().build()))

            return mapper.readValue(configUrl, NodeDefinition::class.java)
        }
    }
}