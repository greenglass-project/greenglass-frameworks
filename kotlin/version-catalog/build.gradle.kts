plugins {
    `version-catalog`
    `maven-publish`
}
group = "io.greenglass"
version = "0.0.2"

publishing {
    publications {
        create<MavenPublication>("version-catalog") {
            from(components["versionCatalog"])
        }
    }
}

catalog {
    // declare the aliases, bundles and versions in this block
    versionCatalog {
        version("kotlin-version", "1.9.23")
        version("kotlin-coroutines-version", "1.7.3")
        version("kotlin-date-time-version", "0.5.0")
        version("kotlin-logging-version", "6.0.3")
        version("kotlin-serialization-json-version", "1.6.3")
        version("kotlin-serialization-yaml-version", "0.58.0")
        version("dokka-version", "1.9.10")
        version("jackson-version", "2.14.2")
        version("commons-math3-version", "3.6.1")
        version("commons-lang3-version", "3.14.0")
        version("guava-version", "32.0.0-android")
        version("tahu-version", "1.0.7")
        version("hivemq-version", "1.3.0")
        version("influxdb-version", "6.12.0")
        version("nats-version", "2.17.1")
        version("neo4j-version", "5.19.0")
        version("settings-version", "1.1.0")
        version("pi4j-version", "2.3.0")
        version("javalin-version", "6.1.3")
        version("oshi-version", "6.5.0")
        version("jpy-version", "0.15.0")
        version("klogging-version","0.5.11")
        version("slf4j-version","2.0.13")
        version("ksp-version","1.9.23-1.0.20")
        version("kotlin-poet-version","1.16.0")
        version("jib-version","3.4.2")

        library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin-version")
        library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin-version")
        library(
            "kotlin-coroutines",
            "org.jetbrains.kotlinx",
            "kotlinx-coroutines-core"
        ).versionRef("kotlin-coroutines-version")
        library(
            "kotlin-datetime",
            "org.jetbrains.kotlinx",
            "kotlinx-datetime-jvm"
        ).versionRef("kotlin-date-time-version")
        library("kotlin-logging", "io.github.oshai", "kotlin-logging-jvm").versionRef("kotlin-logging-version")


        library("jackson-core", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson-version")
        library("jackson-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").versionRef("jackson-version")
        library(
            "jackson-jsr310",
            "com.fasterxml.jackson.datatype",
            "jackson-datatype-jsr310"
        ).versionRef("jackson-version")
        library(
            "jackson-yaml",
            "com.fasterxml.jackson.dataformat",
            "jackson-dataformat-yaml"
        ).versionRef("jackson-version")

        library("commons-math3", "org.apache.commons", "commons-math3").versionRef("commons-math3-version")
        library("commons-lang3", "org.apache.commons","commons-lang3").versionRef("commons-lang3-version")

        library("google-guava", "com.google.guava", "guava").versionRef("guava-version")

        library("tahu", "org.eclipse.tahu", "tahu-core").versionRef("tahu-version")
        library("hivemq", "com.hivemq", "hivemq-mqtt-client").versionRef("hivemq-version")
        library("settings", "com.russhwolf", "multiplatform-settings-no-arg").versionRef("settings-version")
        library("influxdb", "com.influxdb", "influxdb-client-kotlin").versionRef("influxdb-version")
        library("nats", "io.nats", "jnats").versionRef("nats-version")
        library("neo4j", "org.neo4j", "neo4j").versionRef("neo4j-version")
        library("neo4j-bolt", "org.neo4j", "neo4j-bolt").versionRef("neo4j-version")
        library("jpy", "org.jpyconsortium", "jpy").versionRef("jpy-version")

        library("pi4j-core", "com.pi4j", "pi4j-core").versionRef("pi4j-version")
        library("pi4j-pi", "com.pi4j", "pi4j-plugin-raspberrypi").versionRef("pi4j-version")
        library("pi4j-linuxfs", "com.pi4j", "pi4j-plugin-linuxfs").versionRef("pi4j-version")
        library("pi4j-pigpio", "com.pi4j", "pi4j-plugin-pigpio").versionRef("pi4j-version")
        library("oshi-core", "com.github.oshi", "oshi-core").versionRef("oshi-version")
        library("ksp", "com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp-version")

        library("javalin", "io.javalin", "javalin").versionRef("javalin-version")
        library(
            "kotlin-serialization-json",
            "org.jetbrains.kotlinx",
            "kotlinx-serialization-json"
        ).versionRef("kotlin-serialization-json-version")
        library(
            "kotlin-serialization-yaml",
            "com.charleskorn.kaml",
            "kaml"
        ).versionRef("kotlin-serialization-yaml-version")
        library("klogging","io.klogging","klogging-jvm").versionRef("klogging-version")
        library("slf4j","org.slf4j","slf4j-nop").versionRef("slf4j-version")
        library("kotlin-poet","com.squareup","kotlinpoet").versionRef("kotlin-poet-version")
        library("kotlin-poet-ksp","com.squareup","kotlinpoet-ksp").versionRef("kotlin-poet-version")

        plugin("jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin-version")
        plugin("dokka", "org.jetbrains.dokka").versionRef("dokka-version")
        plugin("kotlin-serialisation", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin-version")
        plugin("ksp","com.google.devtools.ksp").versionRef("ksp-version")
        plugin("jib","com.google.cloud.tools.jib").versionRef("jib-version")

    }
}