package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue

interface IRamDataProvider {

    fun getTotalBytes(): Long

    fun getAvailableBytes(): Long

    fun getThreshold(): Long

    fun getAdditionalData(): List<ItemValue>
}
