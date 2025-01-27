package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

class FakeRamDataProvider : IRamDataProvider {

    override fun getTotalBytes(): Long = 1024L

    override fun getAvailableBytes(): Long = 512L

    override fun getThreshold(): Long = 256L

    override fun getAdditionalData(): List<ItemValue> {
        return emptyList()
    }
}
