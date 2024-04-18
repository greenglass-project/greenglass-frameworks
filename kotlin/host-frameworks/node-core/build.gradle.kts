plugins {
	alias(libs.plugins.jvm)
	alias(libs.plugins.kotlin.serialisation)
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

kotlin {
	jvmToolchain(17)
}

publishing {
	publications {
		create<MavenPublication>("node-core") {
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

dependencies {
	implementation(project(":sparkplug"))

	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlin.coroutines)
	implementation(libs.kotlin.logging)
	implementation(libs.kotlin.datetime)
	implementation(libs.kotlin.serialization.json)
	implementation(libs.kotlin.serialization.yaml)

	implementation(libs.hivemq)
	//implementation(libs.jackson.core)
	//implementation(libs.jackson.kotlin)
	//implementation(libs.jackson.yaml)
	//implementation(libs.tahu)

	implementation(libs.pi4j.core)
	implementation(libs.pi4j.pi)
	implementation(libs.pi4j.linuxfs)
	implementation(libs.pi4j.pigpio)

	implementation(libs.settings)
	implementation(libs.javalin)
	implementation(libs.oshi.core)

	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

