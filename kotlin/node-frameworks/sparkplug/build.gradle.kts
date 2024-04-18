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
		create<MavenPublication>("node-sparkplug") {
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

dependencies {
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlin.coroutines)
	implementation(libs.klogging)
	implementation(libs.slf4j)
	implementation(libs.kotlin.datetime)
	implementation(libs.kotlin.serialization.json)

	implementation(libs.hivemq)
	implementation(libs.tahu)

	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

