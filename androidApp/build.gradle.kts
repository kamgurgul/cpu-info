plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
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
    }

    namespace = "com.kgurgul.cpuinfo"

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

    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
    ksp(libs.koin.kspCompiler)

    implementation(libs.relinker)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.espresso.contrib)
    androidTestImplementation(libs.compose.ui.test)

    debugImplementation(libs.compose.ui.testManifest)

    androidTestUtil(libs.androidx.test.orchestrator)

    "baselineProfile"(project(":androidApp:baselineprofile"))
}
