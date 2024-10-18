package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import org.koin.core.component.KoinComponent

actual class StorageDataProvider actual constructor() : KoinComponent {

    actual fun getStorageInfo(): List<StorageItem> {
        return emptyList()
    }
}
