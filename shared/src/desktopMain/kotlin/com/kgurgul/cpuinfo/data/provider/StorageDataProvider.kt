package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_24
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
                add(
                    StorageItem(
                        id = osFileStore.uuid,
                        label = osFileStore.name,
                        iconDrawable = Res.drawable.baseline_folder_24,
                        storageTotal = osFileStore.totalSpace,
                        storageUsed = osFileStore.totalSpace - osFileStore.freeSpace,
                    )
                )
            }
        }.sortedBy { it.label }
    }
}