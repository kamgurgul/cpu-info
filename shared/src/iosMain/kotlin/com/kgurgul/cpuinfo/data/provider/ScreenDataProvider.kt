package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.IosHardwareDataProvider
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.height
import com.kgurgul.cpuinfo.shared.width
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory

@Factory
actual class ScreenDataProvider actual constructor() {

    actual suspend fun getData(): List<Pair<String, String>> {
        val iosHardwareDataProvider = IosHardwareDataProvider.sharedInstance()
        return buildList {
            add(
                Pair(
                    getString(Res.string.width),
                    "${iosHardwareDataProvider.getScreenWidth()}px"
                )
            )
            add(
                Pair(
                    getString(Res.string.height),
                    "${iosHardwareDataProvider.getScreenHeight()}px"
                )
            )
        }
    }
}