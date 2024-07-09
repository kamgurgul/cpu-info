package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.height
import com.kgurgul.cpuinfo.shared.width
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class ScreenDataProvider actual constructor() : KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual suspend fun getData(): List<Pair<String, String>> {
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