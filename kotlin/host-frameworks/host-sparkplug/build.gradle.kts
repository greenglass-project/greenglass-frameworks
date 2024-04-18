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
	archiveClassifier.set("sources")
	from(sourceSets.getByName("main").allSource)
}

publishing {
	publications {
		create<MavenPublication>("host-sparkplug") {
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}

dependencies {
    implementation(project(":sparkplug"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.coroutines)
    implementation(libs.tahu)
    implementation(libs.hivemq)
    implementation(libs.kotlin.logging)
    implementation(libs.commons.math3)
    implementation(libs.google.guava)
    implementation(libs.settings)

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

