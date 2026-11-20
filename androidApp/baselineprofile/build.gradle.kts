import com.kgurgul.AndroidVersions

plugins {
    alias(libs.plugins.androidTest)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.kgurgul.cpuinfo.baselineprofile"
    compileSdk {
        version =
            release(AndroidVersions.COMPILE_SDK) {
                minorApiLevel = AndroidVersions.COMPILE_SDK_MINOR
            }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdk = 28
        targetSdk = AndroidVersions.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":androidApp"

    testOptions.managedDevices.allDevices {
        create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api33") {
            device = "Pixel 6"
            apiLevel = 33
            systemImageSource = "aosp"
        }
    }
}

baselineProfile {
    managedDevices += "pixel6Api33"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.uiautomator)
}

androidComponents {
    onVariants { v ->
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { v.artifacts.getBuiltArtifactsLoader().load(it)?.applicationId!! },
        )
    }
}
