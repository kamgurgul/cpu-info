package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class StorageDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val diskStores = systemInfo.hardware.diskStores

    actual fun getStorageInfo(): List<StorageItem> {
        return emptyList()
    }
}