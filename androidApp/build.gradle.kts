plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.mockito.kotlin)
                implementation(libs.turbine)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.kgurgul.cpuinfo"

        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_static"
            }
        }
    }

    namespace = "com.kgurgul.cpuinfo"

    ndkVersion = Versions.NDK_VERSION

    signingConfigs {
        getByName("debug") {
            val debugSigningConfig = SigningConfig.getReleaseProperties(rootProject.rootDir)
            storeFile = file(debugSigningConfig.getProperty(SigningConfig.KEY_PATH))
            keyAlias = debugSigningConfig.getProperty(SigningConfig.KEY_ALIAS)
            keyPassword = debugSigningConfig.getProperty(SigningConfig.KEY_PASS)
            storePassword = debugSigningConfig.getProperty(SigningConfig.KEY_PASS)
        }
        create("release") {
            val releaseSigningConfig = SigningConfig.getReleaseProperties(rootProject.rootDir)
            storeFile = file(releaseSigningConfig.getProperty(SigningConfig.KEY_PATH))
            keyAlias = releaseSigningConfig.getProperty(SigningConfig.KEY_ALIAS)
            keyPassword = releaseSigningConfig.getProperty(SigningConfig.KEY_PASS)
            storePassword = releaseSigningConfig.getProperty(SigningConfig.KEY_PASS)
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            applicationIdSuffix = ".release"
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            applicationIdSuffix = ".debug"
        }
    }

    externalNativeBuild {
        cmake {
            path("src/androidMain/cpp/CMakeLists.txt")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    lint {
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.koin.android)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.contrib)

    androidTestUtil(libs.androidx.test.orchestrator)

    //"baselineProfile"(project(":baselineprofile"))
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
