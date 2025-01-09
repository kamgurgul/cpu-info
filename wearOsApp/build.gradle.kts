plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
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
    }
}

android {
    compileSdk = AndroidVersions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.kgurgul.cpuinfo"

        minSdk = AndroidVersions.WEAR_MIN_SDK
        targetSdk = AndroidVersions.TARGET_SDK
        versionCode = AndroidVersions.WEAR_VERSION_CODE
        versionName = AndroidVersions.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    namespace = "com.kgurgul.cpuinfo.wear"

    signingConfigs {
        getByName("debug") {
            val debugSigningConfig = AndroidSigningConfig.getDebugProperties(rootProject.rootDir)
            storeFile = file(debugSigningConfig.getProperty(AndroidSigningConfig.KEY_PATH))
            keyAlias = debugSigningConfig.getProperty(AndroidSigningConfig.KEY_ALIAS)
            keyPassword = debugSigningConfig.getProperty(AndroidSigningConfig.KEY_PASS)
            storePassword = debugSigningConfig.getProperty(AndroidSigningConfig.KEY_PASS)
        }
        create("release") {
            val releaseSigningConfig =
                AndroidSigningConfig.getReleaseProperties(rootProject.rootDir)
            storeFile = file(releaseSigningConfig.getProperty(AndroidSigningConfig.KEY_PATH))
            keyAlias = releaseSigningConfig.getProperty(AndroidSigningConfig.KEY_ALIAS)
            keyPassword = releaseSigningConfig.getProperty(AndroidSigningConfig.KEY_PASS)
            storePassword = releaseSigningConfig.getProperty(AndroidSigningConfig.KEY_PASS)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.wear.foundation)
    implementation(libs.androidx.wear.material)
    implementation(libs.androidx.wear.navigation)
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)

    implementation(libs.koin.android)
}
