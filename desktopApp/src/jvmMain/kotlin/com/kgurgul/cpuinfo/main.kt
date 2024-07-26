package com.kgurgul.cpuinfo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kgurgul.cpuinfo.di.initKoin

fun main() {
    initKoin()
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CPU Info",
        ) {
            DesktopApp()
        }
    }
}
