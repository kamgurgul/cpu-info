package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import org.koin.core.annotation.Factory

@Factory
expect class StorageDataProvider() {

    fun getStorageInfo(): List<StorageItem>
}