package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

interface IOsDataProvider {

    suspend fun getData(): List<ItemValue>
}
