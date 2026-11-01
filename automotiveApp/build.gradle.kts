import com.kgurgul.AndroidSigningConfig
import com.kgurgul.AndroidVersions

plugins {
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = AndroidVersions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.kgurgul.cpuinfo"

        minSdk = AndroidVersions.AUTOMOTIVE_MIN_SDK
        targetSdk = AndroidVersions.TARGET_SDK
        versionCode = AndroidVersions.AUTOMOTIVE_VERSION_CODE
        versionName = AndroidVersions.VERSION_NAME
    }

    namespace = "com.kgurgul.cpuinfo.automotive"

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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
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
    implementation(projects.androidAutoApp)
    implementation(projects.shared)
    implementation(libs.androidx.car.app.automotive)
}
