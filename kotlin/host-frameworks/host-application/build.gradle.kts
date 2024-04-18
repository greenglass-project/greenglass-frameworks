plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.dokka)
    `maven-publish`
}

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.value("sources")
}

publishing {
    publications {
        create<MavenPublication>("host-application") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

dependencies {
    implementation(project(":sparkplug"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlin.datetime)
    implementation(libs.nats)
    implementation(libs.neo4j)
    implementation(libs.influxdb)
    implementation(libs.jackson.core)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.jsr310)
    implementation(libs.jackson.yaml)
    implementation(libs.settings)
    implementation(libs.javalin)
    implementation(libs.jpy)

}