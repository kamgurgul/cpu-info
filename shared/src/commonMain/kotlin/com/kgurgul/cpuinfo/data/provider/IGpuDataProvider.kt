package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

interface IGpuDataProvider {

    suspend fun getData(): List<ItemValue>
}
