rootProject.name = "kotlin-frameworks"

include("sparkplug")
include("host-application")
include("host-control")
include("host-sparkplug")
include("node-core")
include("dependency-catalog")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
