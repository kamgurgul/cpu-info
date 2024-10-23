package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import com.kgurgul.cpuinfo.utils.getTotalStorage
import com.kgurgul.cpuinfo.utils.getUsedStorage
import kotlinx.coroutines.await
import org.koin.core.component.KoinComponent

actual class StorageDataProvider actual constructor() : KoinComponent {

    actual suspend fun getStorageInfo(): List<StorageItem> {
        return listOf(
            StorageItem(
                id = "0",
                label = "Estimated: ",
                iconDrawable = Res.drawable.ic_hard_drive,
                storageTotal = getTotalStorage().await<JsBigInt>().toLong(),
                storageUsed = getUsedStorage().await<JsBigInt>().toLong(),
            )
        )
    }
}
