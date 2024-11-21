package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.model
import com.kgurgul.cpuinfo.shared.os_jailbroken
import com.kgurgul.cpuinfo.shared.os_multitasking_supported
import com.kgurgul.cpuinfo.shared.os_vendor_identifier
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.utils.ResourceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UIKit.UIDevice

actual class OsDataProvider actual constructor() :
    IOsDataProvider,
    KoinComponent {

    private val iosSoftwareDataProvider: IosSoftwareDataProvider by inject()

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.tab_os, UIDevice.currentDevice.systemName))
            add(ItemValue.NameResource(Res.string.version, UIDevice.currentDevice.systemVersion))
            add(ItemValue.NameResource(Res.string.model, UIDevice.currentDevice.model))
            add(
                ItemValue.NameValueResource(
                    Res.string.os_multitasking_supported,
                    ResourceUtils.getYesNoStringResource(
                        UIDevice.currentDevice.multitaskingSupported,
                    ),
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.os_vendor_identifier,
                    UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "",
                )
            )
            add(
                ItemValue.NameValueResource(
                    Res.string.os_jailbroken,
                    ResourceUtils.getYesNoStringResource(iosSoftwareDataProvider.isJailBroken()),
                )
            )
        }
    }
}
