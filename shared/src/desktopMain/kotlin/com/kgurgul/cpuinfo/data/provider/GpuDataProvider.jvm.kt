package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.id
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.shared.vram
import com.kgurgul.cpuinfo.utils.Utils
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class GpuDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val graphicsCards = systemInfo.hardware.graphicsCards

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            graphicsCards.forEach { gpu ->
                add(gpu.name to "")
                add(getString(Res.string.vendor) to gpu.vendor)
                add(getString(Res.string.id) to gpu.deviceId)
                if (gpu.vRam != 0L) {
                    add(getString(Res.string.vram) to Utils.humanReadableByteCount(gpu.vRam))
                }
                add(getString(Res.string.version) to gpu.versionInfo)
            }
        }
    }
}
