package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

expect class OsDataProvider() : IOsDataProvider {

    override suspend fun getData(): List<ItemValue>
}
