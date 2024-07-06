package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.shared.IosHardwareDataProvider
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.internal
import org.koin.core.annotation.Factory

@Factory
actual class StorageDataProvider actual constructor() {

    actual fun getStorageInfo(): List<StorageItem> {
        return listOfNotNull(getInternalStorage())
    }

    private fun getInternalStorage(): StorageItem? {
        val storageTotal = IosHardwareDataProvider.sharedInstance().getTotalDiskSpaceInBytes()
        val availableSpace = IosHardwareDataProvider.sharedInstance().getFreeDiskSpaceInBytes()
        val storageUsed = storageTotal - availableSpace
        return if (storageTotal > 0) {
            StorageItem(
                id = 0L,
                label = Res.string.internal,
                iconDrawable = Res.drawable.baseline_folder_special_24,
                storageTotal = storageTotal,
                storageUsed = storageUsed,
            )
        } else {
            null
        }
    }
}