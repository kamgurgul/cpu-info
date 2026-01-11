import com.kgurgul.AndroidVersions
import com.kgurgul.KoverConfig
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.licenses)
    alias(libs.plugins.serialization)
    id("kotlin-parcelize")
}

version = "1.0"

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.kgurgul.cpuinfo.shared"
        compileSdk = AndroidVersions.COMPILE_SDK
        minSdk = AndroidVersions.MIN_SDK

        androidResources {
            enable = true
        }

        withHostTestBuilder {}.configure {}

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    val iosTargets = listOf(iosX64(), iosArm64())
    iosTargets.forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            binaryOption("bundleId", "com.kgurgul.cpuinfo.shared")
        }

        val baseCinteropPath = "$projectDir/src/nativeInterop/cinterop/"
        val libcpuinfoPath = when (iosTarget.name) {
            "iosX64" -> "${baseCinteropPath}libcpuinfo/libcpuinfo.xcframework/ios-x86_64-simulator/"
            else -> "${baseCinteropPath}libcpuinfo/libcpuinfo.xcframework/ios-arm64/"
        }
        iosTarget.compilations.getByName("main") {
            val libcpuinfo by cinterops.creating {
                definitionFile.set(
                    project.file("src/nativeInterop/cinterop/libcpuinfo/libcpuinfo.def"),
                )
                compilerOpts(
                    "-framework",
                    "libcpuinfo",
                    "-F$libcpuinfoPath",
                )
            }
        }
        iosTarget.binaries.all {
            linkerOpts(
                "-framework",
                "libcpuinfo",
                "-F$libcpuinfoPath",
            )
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                enabled = false
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlin.js.ExperimentalWasmJsInterop")
            }
        }

        commonMain {
            dependencies {
                api(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                api(compose.foundation)
                implementation(compose.materialIconsExtended)
                api(compose.material3)
                implementation(compose.material3AdaptiveNavigationSuite)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)
                api(libs.androidx.navigation.compose)
                api(libs.coil)
                implementation(libs.compose.adaptive)
                implementation(libs.kermit.kermit)
                api(libs.koin.compose.viewodel)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.immutable)
                api(libs.kotlinx.serialization)
            }
        }

        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
        }

        androidMain {
            dependsOn(mobileMain)
            dependencies {
                implementation(project(":native-android"))
                implementation(compose.preview)
                implementation(compose.uiTooling)
                api(libs.androidx.activity.compose)
                implementation(libs.androidx.core)
                api(libs.androidx.core.splashscreen)
                api(libs.androidx.tv)
                api(libs.koin.android)
                implementation(libs.relinker)
            }
        }

        iosMain {
            dependsOn(mobileMain)
        }

        val desktopMain by getting {
            dependsOn(mobileMain)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.imageio.icns)
                implementation(libs.jna)
                implementation(libs.jna.platform)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.oshi)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(libs.kotlinx.browser)
                implementation(npm("cross-spawn", "7.0.6"))
                implementation(npm("path-to-regexp", "0.1.12"))
            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        val desktopTest by getting {
            dependencies {
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        getByName("androidHostTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.kgurgul.cpuinfo.shared"
    generateResClass = always
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

kover {
    reports {
        total {
            filters {
                includes {
                    packages(KoverConfig.includedPackages)
                }
                excludes {
                    packages(KoverConfig.excludedPackages)
                    classes(KoverConfig.excludedClasses)
                    annotatedBy(KoverConfig.excludedAnnotations)
                }
            }

            html {
                htmlDir = layout.buildDirectory.dir("coverage-report/html")
            }

            xml {
                xmlFile = layout.buildDirectory.file("coverage-report/result.xml")
            }
        }
    }
}
