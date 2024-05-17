
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.dokka)
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("host-control") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.logging)
    implementation(libs.jackson.core)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.jsr310)
    implementation(libs.tahu)
    implementation(libs.hivemq)
    implementation(libs.commons.math3)
    implementation(libs.google.guava)
    implementation(libs.tahu)
    implementation(libs.javalin)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

}
