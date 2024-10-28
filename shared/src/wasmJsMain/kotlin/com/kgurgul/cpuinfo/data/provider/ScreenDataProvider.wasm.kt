package com.kgurgul.cpuinfo.data.provider

import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.component.KoinComponent

actual class ScreenDataProvider actual constructor() : KoinComponent {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add("Inner width" to "${window.innerWidth}px")
            add("Inner height" to "${window.innerHeight}px")
            add("Screen available width" to "${window.screen.availWidth}px")
            add("Screen available height" to "${window.screen.availHeight}px")
        }
    }

    actual fun getOrientationFlow(): Flow<String> = emptyFlow()
}
