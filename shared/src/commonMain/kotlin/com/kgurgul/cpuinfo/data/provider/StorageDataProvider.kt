package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem

expect class StorageDataProvider() {

    fun getStorageInfo(): List<StorageItem>
}
