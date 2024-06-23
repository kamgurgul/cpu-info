package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import org.koin.core.annotation.Factory

@Factory
actual class StorageDataProvider actual constructor() {

    actual fun getStorageInfo(): List<StorageItem> {
        return emptyList()
    }
}