rootProject.name = "kotlin-frameworks"

include("host-application")
include("host-control")
include("host-sparkplug")
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
