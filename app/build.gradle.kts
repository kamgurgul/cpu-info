plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kover)
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
        multiDexEnabled = true
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
            val debugSigningConfig = SigningConfig.getDebugProperties(rootProject.rootDir)
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
            path("src/main/cpp/CMakeLists.txt")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        aidl = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.compilerVersion
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.immutable)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.activityKtx)
    implementation(Libs.AndroidX.fragmentKtx)
    implementation(Libs.AndroidX.appCompat)
    implementation(Libs.AndroidX.preference)
    implementation(Libs.AndroidX.swipeRefreshLayout)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.multiDex)
    implementation(Libs.AndroidX.viewPager2)
    implementation(Libs.AndroidX.datastorePreferences)

    implementation(Libs.AndroidX.Lifecycle.viewModelKtx)
    implementation(Libs.AndroidX.Lifecycle.runtimeCompose)
    implementation(Libs.AndroidX.Lifecycle.liveDataKtx)
    implementation(Libs.AndroidX.Lifecycle.common)

    val composeBom = platform(Libs.AndroidX.Compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.material3)
    implementation(Libs.AndroidX.Compose.animations)
    implementation(Libs.AndroidX.Compose.uiToolingPreview)
    debugImplementation(Libs.AndroidX.Compose.uiTooling)
    debugImplementation(Libs.AndroidX.Compose.uiTestManifest)

    implementation(Libs.AndroidX.Navigation.fragment)
    implementation(Libs.AndroidX.Navigation.compose)
    implementation(Libs.AndroidX.Navigation.ui)

    implementation(Libs.Google.material)
    implementation(Libs.Google.gson)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.timber)
    implementation(libs.relinker)
    implementation(libs.coil)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(Libs.AndroidX.Test.core)
    testImplementation(Libs.AndroidX.Test.archCoreTesting)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.hilt.android.testing)
    testImplementation(Libs.Mockito.core)
    testImplementation(Libs.Mockito.kotlin)
    kspTest(libs.hilt.compiler)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.rules)
    androidTestImplementation(Libs.AndroidX.Test.jUnitExt)
    androidTestImplementation(Libs.AndroidX.Test.Espresso.core)
    androidTestImplementation(Libs.AndroidX.Test.Espresso.contrib)
    androidTestImplementation(Libs.AndroidX.Compose.uiTest)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    androidTestUtil(Libs.AndroidX.Test.orchestrator)
}

koverReport {
    androidReports("debug") {
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
            setReportDir(layout.buildDirectory.dir("coverage-report/html"))
        }

        xml {
            setReportFile(layout.buildDirectory.file("coverage-report/result.xml"))
        }
    }
}
