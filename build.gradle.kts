import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dependencyUpdate)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

subprojects {
    tasks.withType<KotlinCompile>().all {
        compilerOptions {
            //allWarningsAsErrors = true
            jvmTarget = JvmTarget.JVM_11

            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            project.layout.buildDirectory.dir("compose_compiler").get()
                                .asFile.absolutePath
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs.addAll(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                            project.layout.buildDirectory.dir("compose-metrics").get()
                                .asFile.absolutePath
                )
            }
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
