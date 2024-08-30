import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(project(":shared"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.kgurgul.cpuinfo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Pkg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CPU-Info"
            packageVersion = "1.3.0"

            val iconsRoot = project.file("desktop-icons")
            macOS {
                val isAppStoreRelease = project.property("macOsAppStoreRelease")
                    .toString()
                    .toBoolean()
                bundleID = "com.kgurgul.cpuinfo"
                iconFile.set(iconsRoot.resolve("icon-mac.icns"))
                appStore = isAppStoreRelease
                minimumSystemVersion = "12.0"
                if (isAppStoreRelease) {
                    provisioningProfile.set(project.file("embedded.provisionprofile"))
                    runtimeProvisioningProfile.set(project.file("runtime.provisionprofile"))
                }
            }
            windows {
                iconFile.set(iconsRoot.resolve("icon-windows.ico"))
                menuGroup = "CPU-Info"
                upgradeUuid = "d4a688eb-758f-44e8-81bb-8367880c5c95"
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon-linux.png"))
            }
        }

        buildTypes.release.proguard {
            isEnabled = false
            configurationFiles.from("rules.pro")
        }
    }
}