import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
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
        iosTarget.compilations.getByName("main") {
            val libcpuinfo by cinterops.creating {
                definitionFile.set(
                    project.file("src/nativeInterop/cinterop/libcpuinfo/libcpuinfo.def")
                )
                compilerOpts(
                    "-framework",
                    "libcpuinfo",
                    "-F$projectDir/src/nativeInterop/cinterop/libcpuinfo/"
                )
            }
        }
        iosTarget.binaries.all {
            linkerOpts(
                "-framework",
                "libcpuinfo",
                "-F$projectDir/src/nativeInterop/cinterop/libcpuinfo/"
            )
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("org.koin.core.annotation.KoinExperimentalAPI")
            }
        }

        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                api(compose.components.resources)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(libs.androidx.datastore.preferences)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.coil)
                implementation(libs.kermit.kermit)
                implementation(libs.koin.annotations)
                implementation(libs.koin.compose.viewodel)
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.immutable)
            }
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(compose.uiTooling)
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.mockito.kotlin)
                implementation(libs.turbine)
            }
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.kgurgul.cpuinfo.shared"
    generateResClass = always
}

android {
    compileSdk = Versions.COMPILE_SDK
    namespace = "com.kgurgul.cpuinfo.shared"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    defaultConfig {
        minSdk = Versions.MIN_SDK
        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_static"
            }
        }
    }
    ndkVersion = Versions.NDK_VERSION
    externalNativeBuild {
        cmake {
            path("src/androidMain/cpp/CMakeLists.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
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

dependencies {
    add("kspCommonMainMetadata", libs.koin.kspCompiler)
    //add("kspAndroid", libs.koin.kspCompiler)
    //add("kspIosX64", libs.koin.kspCompiler)
    //add("kspIosArm64", libs.koin.kspCompiler)
    //add("kspIosSimulatorArm64", libs.koin.kspCompiler)
}

tasks.withType<KotlinCompile>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
afterEvaluate {
    tasks.filter {
        it.name.contains("SourcesJar", true)
    }.forEach {
        println("SourceJarTask====>${it.name}")
        it.dependsOn("kspCommonMainKotlinMetadata")
    }
}
