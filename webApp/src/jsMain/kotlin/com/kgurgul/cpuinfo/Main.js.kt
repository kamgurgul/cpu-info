package com.kgurgul.cpuinfo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.kgurgul.cpuinfo.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    CanvasBasedWindow("CPU Info") {
        WebApp()
    }
}
