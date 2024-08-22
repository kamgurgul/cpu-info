package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class StorageDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val fileStores = systemInfo.operatingSystem.fileSystem.fileStores

    actual fun getStorageInfo(): List<StorageItem> {
        return buildList {
            fileStores.forEach { osFileStore ->
                val label = buildString {
                    appendLine(osFileStore.name)
                    appendLine(osFileStore.type)
                }
                add(
                    StorageItem(
                        id = osFileStore.uuid,
                        label = label,
                        iconDrawable = Res.drawable.ic_hard_drive,
                        storageTotal = osFileStore.totalSpace,
                        storageUsed = osFileStore.totalSpace - osFileStore.freeSpace,
                    )
                )
            }
        }.distinctBy { it.id }
            .sortedBy { it.label }
    }
}