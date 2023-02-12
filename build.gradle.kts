import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.gradleVersionsPlugin)
        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.Hilt.gradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

apply(plugin = "com.github.ben-manes.versions")

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            @Suppress("SuspiciousCollectionReassignment")
            freeCompilerArgs += listOf(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
            allWarningsAsErrors = true
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
}

/**
 * Update dependencyUpdates task to reject versions which are more 'unstable' than our
 * current version.
 */
tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        val current = DependencyUpdates.versionToRelease(currentVersion)
        // If we're using a SNAPSHOT, ignore since we must be doing so for a reason.
        if (current == ReleaseType.SNAPSHOT) {
            true
        } else {
            // Otherwise we reject if the candidate is more 'unstable' than our version
            DependencyUpdates.versionToRelease(candidate.version).isLessStableThan(current)
        }
    }
}
