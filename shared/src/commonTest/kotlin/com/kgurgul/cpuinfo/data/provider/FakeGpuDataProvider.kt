package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.model.ItemValue

class FakeGpuDataProvider : IGpuDataProvider {

    override suspend fun getData(): List<ItemValue> {
        return TestData.gpuData
    }
}
