package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem

interface IStorageDataProvider {

    suspend fun getStorageInfo(): List<StorageItem>
}
