rootProject.name = "cpu-info"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("io.github.ben-manes.versions.settings") version "0.59.0"
}

include(":androidApp")
include(":androidApp:baselineprofile")
include(":desktopApp")
include(":native-android")
include(":shared")
include(":wearOsApp")
include(":webApp")
