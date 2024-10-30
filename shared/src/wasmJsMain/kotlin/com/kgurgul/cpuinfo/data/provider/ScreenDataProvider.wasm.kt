package com.kgurgul.cpuinfo.data.provider

import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class ScreenDataProvider actual constructor() {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add("Inner width" to "${window.innerWidth}px")
            add("Inner height" to "${window.innerHeight}px")
            add("Screen available width" to "${window.screen.availWidth}px")
            add("Screen available height" to "${window.screen.availHeight}px")
            add("Color depth" to window.screen.colorDepth.toString())
            add("Pixel depth" to window.screen.pixelDepth.toString())
        }
    }

    actual fun getOrientationFlow(): Flow<String> = emptyFlow()
}
