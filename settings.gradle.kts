rootProject.name = "cpu-info"

pluginManagement {
    repositories {
        google()
        maven {
            setUrl("${rootProject.projectDir}/external/m2/repository")
        }
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

include(":androidApp")
include(":shared")
include(":baselineprofile")
