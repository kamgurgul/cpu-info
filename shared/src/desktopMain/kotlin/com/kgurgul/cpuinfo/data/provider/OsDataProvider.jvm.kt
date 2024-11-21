package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.manufacturer
import com.kgurgul.cpuinfo.shared.os_language
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import java.util.Locale
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class OsDataProvider actual constructor() :
    IOsDataProvider,
    KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override suspend fun getData(): List<ItemValue> {
        val operatingSystem = systemInfo.operatingSystem
        return buildList {
            add(ItemValue.NameResource(Res.string.tab_os, operatingSystem.family))
            add(ItemValue.NameResource(Res.string.version, operatingSystem.versionInfo.toString()))
            add(ItemValue.NameResource(Res.string.manufacturer, operatingSystem.manufacturer))
            add(ItemValue.NameResource(Res.string.os_language, Locale.getDefault().language))
        }
    }
}
