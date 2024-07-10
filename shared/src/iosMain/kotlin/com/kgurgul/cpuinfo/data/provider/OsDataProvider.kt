package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.tab_os
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory

@Factory
actual class OsDataProvider actual constructor() {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getString(Res.string.tab_os) to "iOS")
        }
    }
}