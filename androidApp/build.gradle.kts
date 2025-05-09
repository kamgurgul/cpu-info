import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.JsonReportRenderer
import com.github.jk1.license.render.ReportRenderer

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.licenses)
    alias(libs.plugins.serialization)
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

        minSdk = AndroidVersions.MIN_SDK
        targetSdk = AndroidVersions.TARGET_SDK
        versionCode = AndroidVersions.VERSION_CODE
        versionName = AndroidVersions.VERSION_NAME

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    namespace = "com.kgurgul.cpuinfo"

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

baselineProfile {
    saveInSrc = true
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

licenseReport {
    renderers = arrayOf<ReportRenderer>(JsonReportRenderer("licenses.json"))
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
}

dependencies {
    implementation(libs.androidx.profileinstaller)

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
