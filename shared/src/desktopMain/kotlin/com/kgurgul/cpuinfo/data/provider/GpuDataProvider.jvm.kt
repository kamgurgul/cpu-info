package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.id
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.shared.vram
import com.kgurgul.cpuinfo.utils.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class GpuDataProvider actual constructor() : IGpuDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override suspend fun getData(): List<ItemValue> {
        val graphicsCards = systemInfo.hardware.graphicsCards
        return buildList {
            graphicsCards.forEach { gpu ->
                add(ItemValue.Text(gpu.name, ""))
                add(ItemValue.NameResource(Res.string.vendor, gpu.vendor))
                add(ItemValue.NameResource(Res.string.id, gpu.deviceId))
                if (gpu.vRam != 0L) {
                    add(
                        ItemValue.NameResource(
                            Res.string.vram,
                            Utils.humanReadableByteCount(gpu.vRam)
                        )
                    )
                }
                add(ItemValue.NameResource(Res.string.version, gpu.versionInfo))
            }
        }
    }
}
