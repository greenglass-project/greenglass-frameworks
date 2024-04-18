package io.greenglass.host.application.neo4j

import io.github.oshai.kotlinlogging.KotlinLogging
import org.neo4j.configuration.GraphDatabaseSettings
import org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME
import org.neo4j.configuration.connectors.BoltConnector
import org.neo4j.configuration.helpers.SocketAddress
import org.neo4j.dbms.api.DatabaseManagementService
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder
import org.neo4j.graphdb.GraphDatabaseService
import java.time.Duration
import kotlin.io.path.Path

class Neo4jService(val directory : String) {

    private val logger = KotlinLogging.logger {}

    private val managementService : DatabaseManagementService = DatabaseManagementServiceBuilder(Path(directory))
        .setConfig(GraphDatabaseSettings.pagecache_memory, 512*1024*1024)
        .setConfig(GraphDatabaseSettings.transaction_timeout, Duration.ofSeconds(60))
       // .setConfig(GraphDatabaseSettings.preallocate_logical_logs, true)

        .setConfig( BoltConnector.enabled, true )
        .setConfig( BoltConnector.listen_address, SocketAddress( "localhost", 7687 ))
        .build()

    var db: GraphDatabaseService = managementService.database(DEFAULT_DATABASE_NAME)

    init {
        logger.debug { "Starting neo4j in directory $directory"}
        registerShutdownHook(managementService);
    }

    private fun registerShutdownHook(managementService: DatabaseManagementService) {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                managementService.shutdown()
            }
        })
    }
}