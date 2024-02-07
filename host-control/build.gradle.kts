
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

publishing {
    publications {
        create<MavenPublication>("-control") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

dependencies {
    implementation(project(":sparkplug"))
    implementation(project(":host-sparkplug"))
    implementation(project(":host-application"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.logging)
    implementation(libs.tahu)
}
