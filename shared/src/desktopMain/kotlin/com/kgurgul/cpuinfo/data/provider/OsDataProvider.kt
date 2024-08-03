package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.manufacturer
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class OsDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val operatingSystem = systemInfo.operatingSystem

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getString(Res.string.tab_os) to operatingSystem.family)
            add(getString(Res.string.version) to operatingSystem.versionInfo.toString())
            add(getString(Res.string.manufacturer) to operatingSystem.manufacturer)
        }
    }
}