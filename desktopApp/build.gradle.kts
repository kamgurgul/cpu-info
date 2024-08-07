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
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CPUInfo"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("desktop-icons")
             macOS {
                 iconFile.set(iconsRoot.resolve("icon-mac.icns"))
             }
             windows {
                 iconFile.set(iconsRoot.resolve("icon-windows.ico"))
                 menuGroup = "CPU Info"
                 upgradeUuid = "d4a688eb-758f-44e8-81bb-8367880c5c95"
             }
             linux {
                 iconFile.set(iconsRoot.resolve("icon-linux.png"))
             }
        }
    }
}