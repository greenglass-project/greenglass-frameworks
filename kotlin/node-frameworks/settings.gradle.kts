rootProject.name = "node-frameworks"
include("node-core")
include("sparkplug")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from("io.greenglass:version-catalog:0.0.2")
        }
    }
}
