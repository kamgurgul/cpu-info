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

            /* val iconsRoot = project.file("desktop-icons")
             macOS {
                 iconFile.set(iconsRoot.resolve("icon-mac.icns"))
             }
             windows {
                 iconFile.set(iconsRoot.resolve("icon-windows.ico"))
                 menuGroup = "Compose Examples"
                 // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                 upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
             }
             linux {
                 iconFile.set(iconsRoot.resolve("icon-linux.png"))
             }*/
        }
    }
}