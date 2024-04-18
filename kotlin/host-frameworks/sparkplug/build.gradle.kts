plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kotlin.serialisation)
    alias(libs.plugins.dokka)
    `maven-publish`
}

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
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
		create<MavenPublication>("sparkplug") {
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.coroutines)
    implementation(libs.tahu)

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")

}

