package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.manufacturer
import com.kgurgul.cpuinfo.shared.os_language
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import java.util.Locale
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class OsDataProvider actual constructor() :
    IOsDataProvider,
    KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override suspend fun getData(): List<Pair<String, String>> {
        val operatingSystem = systemInfo.operatingSystem
        return buildList {
            add(getString(Res.string.tab_os) to operatingSystem.family)
            add(getString(Res.string.version) to operatingSystem.versionInfo.toString())
            add(getString(Res.string.manufacturer) to operatingSystem.manufacturer)
            add(getString(Res.string.os_language) to Locale.getDefault().language)
        }
    }
}
