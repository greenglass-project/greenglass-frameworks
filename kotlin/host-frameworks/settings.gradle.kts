rootProject.name = "host-frameworks"

include("host-application")
include("host-control")


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
