import com.kgurgul.AndroidVersions

plugins {
    alias(libs.plugins.android.library)
}

android {
    compileSdk = AndroidVersions.COMPILE_SDK
    namespace = "com.kgurgul.cpuinfo.nativelib"

    defaultConfig {
        minSdk = AndroidVersions.MIN_SDK
        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_STL=c++_static")
            }
        }
    }

    ndkVersion = AndroidVersions.NDK_VERSION

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
