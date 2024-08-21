package com.kgurgul.cpuinfo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kgurgul.cpuinfo.di.initKoin
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.desktop_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.DirectContext

fun main() {
    initKoin()
    if (System.getProperty("os.name").equals("Linux", ignoreCase = true)) {
        try {
            DirectContext.makeGL().flush().close()
        } catch (e: Throwable) {
            System.setProperty("skiko.renderApi", "SOFTWARE")
        }
    }
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
