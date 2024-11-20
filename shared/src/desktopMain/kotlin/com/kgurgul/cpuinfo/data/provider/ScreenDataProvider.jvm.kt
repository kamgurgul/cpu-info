package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.edid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class ScreenDataProvider actual constructor() : IScreenDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override suspend fun getData(): List<ItemValue> {
        val displays = systemInfo.hardware.displays
        val displaysEDID = displays.map { it.edid }.joinToString(separator = "\n")
        return buildList {
            if (displaysEDID.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.edid, displaysEDID))
            }
        }
    }

    actual override fun getOrientationFlow(): Flow<String> = emptyFlow()
}
