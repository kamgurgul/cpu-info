package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ram_physical_memory
import com.kgurgul.cpuinfo.shared.ram_physical_memory_info
import com.kgurgul.cpuinfo.utils.Utils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo
import oshi.util.FormatUtil

actual class RamDataProvider actual constructor() : IRamDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override fun getTotalBytes(): Long {
        return systemInfo.hardware.memory.total
    }

    actual override fun getAvailableBytes(): Long {
        return systemInfo.hardware.memory.available
    }

    actual override fun getThreshold(): Long {
        return -1L
    }

    actual override fun getAdditionalData(): List<ItemValue> {
        return buildList {
            if (systemInfo.hardware.memory.physicalMemory.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.ram_physical_memory, ""))
                systemInfo.hardware.memory.physicalMemory.forEach {
                    add(
                        ItemValue.FormattedValueResource(
                            "   ${it.bankLabel}",
                            Res.string.ram_physical_memory_info,
                            listOf(
                                Utils.humanReadableByteCount(it.capacity),
                                FormatUtil.formatHertz(it.clockSpeed),
                                it.manufacturer,
                                it.memoryType,
                                it.partNumber,
                                it.serialNumber,
                            ),
                        )
                    )
                }
            }
        }
    }
}
