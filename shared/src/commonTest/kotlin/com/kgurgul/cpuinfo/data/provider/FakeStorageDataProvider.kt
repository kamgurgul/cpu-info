package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.model.StorageItem

class FakeStorageDataProvider : IStorageDataProvider {

    override suspend fun getStorageInfo(): List<StorageItem> {
        return TestData.storageData
    }
}
