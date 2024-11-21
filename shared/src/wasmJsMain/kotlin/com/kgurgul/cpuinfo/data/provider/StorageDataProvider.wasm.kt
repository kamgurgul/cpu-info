package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import com.kgurgul.cpuinfo.shared.storage_estimated
import com.kgurgul.cpuinfo.utils.getTotalStorage
import com.kgurgul.cpuinfo.utils.getUsedStorage
import kotlinx.coroutines.await

actual class StorageDataProvider actual constructor() : IStorageDataProvider {

    actual override suspend fun getStorageInfo(): List<StorageItem> {
        return listOf(
            StorageItem(
                id = "0",
                label = TextResource.Resource(Res.string.storage_estimated),
                iconDrawable = Res.drawable.ic_hard_drive,
                storageTotal = getTotalStorage().await<JsBigInt>().toLong(),
                storageUsed = getUsedStorage().await<JsBigInt>().toLong(),
            )
        )
    }
}
