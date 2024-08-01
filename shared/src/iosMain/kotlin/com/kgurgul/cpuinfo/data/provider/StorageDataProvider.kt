package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import com.kgurgul.cpuinfo.shared.internal
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class StorageDataProvider actual constructor() : KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual fun getStorageInfo(): List<StorageItem> {
        return listOfNotNull(getInternalStorage())
    }

    private fun getInternalStorage(): StorageItem? {
        val storageTotal = iosHardwareDataProvider.getTotalDiskSpaceInBytes()
        val availableSpace = iosHardwareDataProvider.getFreeDiskSpaceInBytes()
        val storageUsed = storageTotal - availableSpace
        return if (storageTotal > 0) {
            StorageItem(
                id = "0",
                label = runBlocking { getString(Res.string.internal) + ": " },
                iconDrawable = Res.drawable.ic_hard_drive,
                storageTotal = storageTotal,
                storageUsed = storageUsed,
            )
        } else {
            null
        }
    }
}