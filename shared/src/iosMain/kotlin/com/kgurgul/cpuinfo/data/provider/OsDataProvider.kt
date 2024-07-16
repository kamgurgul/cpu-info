package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.model
import com.kgurgul.cpuinfo.shared.os_jailbroken
import com.kgurgul.cpuinfo.shared.os_multitasking_supported
import com.kgurgul.cpuinfo.shared.os_vendor_identifier
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.utils.ResourceUtils
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UIKit.UIDevice

@Factory
actual class OsDataProvider actual constructor() : KoinComponent {

    private val iosSoftwareDataProvider: IosSoftwareDataProvider by inject()

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getString(Res.string.tab_os) to UIDevice.currentDevice.systemName)
            add(getString(Res.string.version) to UIDevice.currentDevice.systemVersion)
            add(getString(Res.string.model) to UIDevice.currentDevice.model)
            add(
                getString(Res.string.os_multitasking_supported) to ResourceUtils.getYesNoString(
                    UIDevice.currentDevice.multitaskingSupported
                )
            )
            add(
                getString(Res.string.os_vendor_identifier) to
                        (UIDevice.currentDevice.identifierForVendor
                            ?.UUIDString ?: getString(Res.string.unknown))
            )
            add(
                getString(Res.string.os_jailbroken) to
                        ResourceUtils.getYesNoString(iosSoftwareDataProvider.isJailBroken())
            )
        }
    }
}