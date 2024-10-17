import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

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
    androidTarget()

    val xcf = XCFramework()
    val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
    iosTargets.forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            binaryOption("bundleId", "com.kgurgul.cpuinfo.shared")
            xcf.add(this)
        }

        val baseCinteropPath = "$projectDir/src/nativeInterop/cinterop/"
        val libcpuinfoPath = when (iosTarget.name) {
            "iosX65" -> "${baseCinteropPath}libcpuinfo/libcpuinfo.xcframework/ios-x86_64-simulator/"
            "iosSimulatorArm64" -> "${baseCinteropPath}libcpuinfo/libcpuinfo.xcframework/ios-arm64-simulator/"
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
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.coil)
                implementation(libs.compose.adaptive)
                implementation(libs.kermit.kermit)
                implementation(libs.koin.compose.viewodel)
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
            dependsOn(mobileMain)
            dependencies {
                implementation(compose.preview)
                implementation(compose.uiTooling)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.koin.android)
                implementation(libs.relinker)
            }
        }

        iosMain {
            dependsOn(mobileMain)
            dependencies {
                implementation(libs.androidx.datastore.preferences)
            }
        }

        val desktopMain by getting
        desktopMain.dependsOn(mobileMain)
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.oshi)
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
                implementation(libs.mockito.kotlin)
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

android {
    compileSdk = AndroidVersions.COMPILE_SDK
    namespace = "com.kgurgul.cpuinfo.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

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
        }
    }
    dependencies {
        debugImplementation(compose.uiTooling)
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
