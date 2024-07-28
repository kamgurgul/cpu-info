package com.kgurgul.cpuinfo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kgurgul.cpuinfo.di.initKoin
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.desktop_icon
import org.jetbrains.compose.resources.painterResource

fun main() {
    initKoin()
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CPU Info",
            icon = painterResource(Res.drawable.desktop_icon),
        ) {
            DesktopApp()
        }
    }
}
