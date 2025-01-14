import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    id("kotlin-parcelize")
}

version = "1.0"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
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

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("mobile") {
                withIos()
                withAndroidTarget()
                withJvm()
            }
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }

        commonMain {
            dependencies {
                api(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                api(compose.foundation)
                api(compose.material3)
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
            }
        }

        val mobileMain by getting {
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.datastore.preferences)
                api(libs.androidx.tv)
                implementation(libs.koin.android)
                implementation(libs.relinker)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.oshi)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        val androidUnitTest by getting {
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

dependencies {
    androidTestImplementation(libs.compose.ui.test)

    debugImplementation(compose.uiTooling)
    debugImplementation(libs.compose.ui.testManifest)
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

android {
    compileSdk = AndroidVersions.COMPILE_SDK
    namespace = "com.kgurgul.cpuinfo.shared"

    defaultConfig {
        minSdk = AndroidVersions.MIN_SDK
        externalNativeBuild {
            cmake {
                arguments += listOf(
                    "-DANDROID_STL=c++_static",
                    "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON",
                )
            }
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    ndkVersion = AndroidVersions.NDK_VERSION
    externalNativeBuild {
        cmake {
            path("src/androidMain/cpp/CMakeLists.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
            all {
                it.exclude("**/screen/**")
            }
        }
    }
}

kover {
    reports {
        variant("debug") {
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
