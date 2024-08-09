package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.edid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class ScreenDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val displays = systemInfo.hardware.displays

    actual suspend fun getData(): List<Pair<String, String>> {
        val displaysEDID = displays.map { it.edid }.joinToString(separator = "\n")
        return buildList {
            if (displaysEDID.isNotEmpty()) {
                add(getString(Res.string.edid) to displaysEDID)
            }
        }
    }

    actual fun getOrientationFlow(): Flow<String> = emptyFlow()
}