package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

expect class HardwareDataProvider() {

    suspend fun getData(): List<ItemValue>
}
