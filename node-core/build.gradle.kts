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
	archiveClassifier.value("sources")
}

publishing {
	publications {
		create<MavenPublication>("node-core") {
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

	implementation(libs.hivemq)
	implementation(libs.jackson.core)
	implementation(libs.jackson.kotlin)
	implementation(libs.jackson.yaml)
	implementation(libs.tahu)

	implementation(libs.pi4j.core)
	implementation(libs.pi4j.pi)
	implementation(libs.pi4j.linuxfs)
	implementation(libs.pi4j.pigpio)

	implementation(libs.settings)

}

