/******************************************************************************
 *  Copyright 2023 Steve Hopkins
 *
 *  Use of this source code is governed by an MIT-style
 *  license that can be found in the LICENSE file or at
 *  https://opensource.org/licenses/MIT.
 *
 *****************************************************************************/
package io.greenglass.node.core.services

import io.klogging.NoCoLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.lang3.RandomStringUtils
import com.charleskorn.kaml.Yaml
import java.io.File

import io.greenglass.node.core.models.NodeDefinition
import io.greenglass.node.core.models.NodeSettings
import io.greenglass.node.core.models.Settings
import io.greenglass.node.sparkplug.BdSeqProvider
import io.greenglass.node.sparkplug.NodeState
import io.greenglass.node.sparkplug.SparkplugHandler


/**
 * ApplicationService
 *
 * Manages the Sparkplug protocol and connection with the broker
 *
 * @property nodeTypes
 * @property persistence
 * @property drivers
 * @property backgroundScope
 */
class ApplicationService(private val configDirectory : String,
                       private val persistence : PersistenceService,
                       private val drivers : DriversService,
                       private val webService: WebService,
                       private val backgroundScope : CoroutineScope
    ) : BdSeqProvider, NoCoLogging {

    private val nodeIdKey = "nodeId"

    /**
     * initialise()
     */
    suspend fun initialise() = coroutineScope {

        // Read the node settings and node description
        val nodeSettings = loadNodeSettings()
        val nodeDefinition = loadNodeType()
        val nodeId = getOrGenerateNodeId()

        logger.info {"================================================"}
        logger.info {"Node-type : ${nodeDefinition.type}"}
        logger.info {"Node-ID   : $nodeId"}
        logger.info {"Group-ID  : ${nodeSettings.groupId}"}
        logger.info {"Host-ID   : ${nodeSettings.hostId}"}
        logger.info {"Broker    : ${nodeSettings.broker}"}
        logger.info {"================================================"}


        // Register the drivers and metrics
        drivers.registerDriversAndMetrics(nodeDefinition)

        // Create the Sparkplug Handler and run it
        val sparkplugHandler = SparkplugHandler(
            nodeType = nodeDefinition.type,
            nodeId = nodeId,
            groupId = nodeSettings.groupId,
            hostId = nodeSettings.hostId,
            broker = nodeSettings.broker,
            metrics = nodeDefinition.metrics,
            drivers = drivers,
            seqPovider = this@ApplicationService,
            backgroundScope = backgroundScope
        )

        // Add the state event publisher
        // this publishes the integer value od the state enum
        backgroundScope.launch {
            val currentStateFlow = sparkplugHandler.state
                .map { ns -> ns.value }
                .transform { e -> emit(Json.encodeToString(IntValue(e))) }
                .stateIn(backgroundScope)
            webService.addEventPublisher("/sparkplug/state", currentStateFlow)
        }

        // Add the settings webservice
        webService.addGet("/sparkplug/settings") { ctx ->
            ctx.json(Json.encodeToString(
                Settings(
                    nodeType = nodeDefinition.type,
                    nodeId = nodeId,
                    nodeSettings = nodeSettings
                )))
        }


        backgroundScope.launch {
            sparkplugHandler.run()
        }
    }

    /**
     * nextBdSeq()
     * Implementation of the BdSequenceProvider interface
     * @return the sequence number
     */
     override fun nextBdSeq(): Long {
        val bdSeq = persistence.getLong("bdSeq", -1)
        val nextBdSeq = bdSeq + 1
        persistence.setLong("bdSeq", nextBdSeq)
        return nextBdSeq
    }

    /**
     * oadNodeSettings()
     *
     * @return
     */
    private fun loadNodeSettings() : NodeSettings {
        val settingsFile = File("$configDirectory/settings.yaml")
        logger.info { "Settings file = $settingsFile"}
        if(!settingsFile.isFile || !settingsFile.exists())
            throw IllegalStateException("Settings file not found : $configDirectory/settings.yaml}")

        return Yaml.default
            .decodeFromString(
                NodeSettings.serializer(),
                settingsFile.readText()
            )
    }

    private fun loadNodeType(): NodeDefinition {
        val definitionFile = checkNotNull(File(configDirectory)
            .walkTopDown()
            .onEach { f -> logger.info { "Found $f"} }
            .filter { f -> f.isFile && f.extension == "yaml" && f.name != "settings.yaml" }
            .firstOrNull(), lazyMessage = { "Node definition not found" }
        )

        return Yaml.default
            .decodeFromString(
                NodeDefinition.serializer(),
                definitionFile.readText()
            )
    }

     private fun getOrGenerateNodeId() : String {
         var nodeId = persistence.getString(nodeIdKey,"")
         if(nodeId.isEmpty()) {
             nodeId = RandomStringUtils.randomAlphanumeric(16)
             persistence.setString(nodeIdKey, nodeId)
             logger.info { "Generated new nodeId $nodeId"}
         }
         return nodeId
    }
}
