import com.kgurgul.AndroidVersions

plugins {
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }
}

android {
    compileSdk = AndroidVersions.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidVersions.MIN_SDK
    }

    namespace = "com.kgurgul.cpuinfo.auto"

    testOptions {
        unitTests {
            isReturnDefaultValues = true
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
    implementation(projects.shared)
    api(libs.androidx.car.app)
    implementation(libs.androidx.core)
    implementation(libs.jetbrains.compose.lifecycle.viewmodel)
    implementation(libs.kermit.kermit)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
