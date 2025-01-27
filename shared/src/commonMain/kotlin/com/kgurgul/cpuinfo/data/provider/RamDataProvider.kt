package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

expect class RamDataProvider() : IRamDataProvider {

    override fun getTotalBytes(): Long

    override fun getAvailableBytes(): Long

    override fun getThreshold(): Long

    override fun getAdditionalData(): List<ItemValue>
}
