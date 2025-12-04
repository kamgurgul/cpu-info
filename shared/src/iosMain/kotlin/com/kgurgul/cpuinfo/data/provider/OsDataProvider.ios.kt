/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.model
import com.kgurgul.cpuinfo.shared.os_active_processors
import com.kgurgul.cpuinfo.shared.os_device_name
import com.kgurgul.cpuinfo.shared.os_jailbroken
import com.kgurgul.cpuinfo.shared.os_language
import com.kgurgul.cpuinfo.shared.os_multitasking_supported
import com.kgurgul.cpuinfo.shared.os_system_uptime
import com.kgurgul.cpuinfo.shared.os_total_processors
import com.kgurgul.cpuinfo.shared.os_vendor_identifier
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.utils.ResourceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSLocale
import platform.Foundation.NSProcessInfo
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIDevice

actual class OsDataProvider actual constructor() : IOsDataProvider, KoinComponent {

    private val iosSoftwareDataProvider: IosSoftwareDataProvider by inject()

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.tab_os, UIDevice.currentDevice.systemName))
            add(ItemValue.NameResource(Res.string.version, UIDevice.currentDevice.systemVersion))
            add(ItemValue.NameResource(Res.string.model, UIDevice.currentDevice.model))
            add(ItemValue.NameResource(Res.string.os_device_name, UIDevice.currentDevice.name))

            add(
                ItemValue.NameValueResource(
                    Res.string.os_multitasking_supported,
                    ResourceUtils.getYesNoStringResource(
                        UIDevice.currentDevice.multitaskingSupported
                    ),
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.os_vendor_identifier,
                    UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "",
                )
            )

            val locale = NSLocale.currentLocale.languageCode
            if (locale != null) {
                add(ItemValue.NameResource(Res.string.os_language, locale))
            }

            val processInfo = NSProcessInfo.processInfo
            add(
                ItemValue.NameResource(
                    Res.string.os_system_uptime,
                    formatUptime(processInfo.systemUptime),
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.os_active_processors,
                    processInfo.activeProcessorCount.toString(),
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.os_total_processors,
                    processInfo.processorCount.toString(),
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

    private fun formatUptime(uptimeSeconds: Double): String {
        val totalSeconds = uptimeSeconds.toLong()
        val days = totalSeconds / 86400
        val hours = (totalSeconds % 86400) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m ${seconds}s"
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}
