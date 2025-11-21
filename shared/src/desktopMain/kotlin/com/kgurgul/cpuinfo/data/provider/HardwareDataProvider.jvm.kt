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
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.hardware_computer_system
import com.kgurgul.cpuinfo.shared.hardware_firmware
import com.kgurgul.cpuinfo.shared.hardware_motherboard
import com.kgurgul.cpuinfo.shared.hardware_network_interfaces
import com.kgurgul.cpuinfo.shared.hardware_power_sources
import com.kgurgul.cpuinfo.shared.hardware_uuid
import com.kgurgul.cpuinfo.shared.manufacturer
import com.kgurgul.cpuinfo.shared.model
import com.kgurgul.cpuinfo.shared.serial
import com.kgurgul.cpuinfo.shared.sound_card
import com.kgurgul.cpuinfo.shared.temperature
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class HardwareDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()

    private val temperatureFormatter: TemperatureFormatter by inject()

    actual suspend fun getData(): List<ItemValue> {
        val hardware = systemInfo.hardware
        return buildList {
            add(ItemValue.NameResource(Res.string.hardware_computer_system, ""))
            add(
                ItemValue.NameResource(
                    Res.string.manufacturer,
                    hardware.computerSystem.manufacturer,
                )
            )
            add(ItemValue.NameResource(Res.string.model, hardware.computerSystem.model))
            add(ItemValue.NameResource(Res.string.serial, hardware.computerSystem.serialNumber))
            add(
                ItemValue.NameResource(
                    Res.string.hardware_uuid,
                    hardware.computerSystem.hardwareUUID,
                )
            )
            val firmware =
                buildString {
                        if (hardware.computerSystem.firmware.manufacturer != UNKNOWN) {
                            append(hardware.computerSystem.firmware.manufacturer)
                            append(" ")
                        }
                        if (hardware.computerSystem.firmware.name != UNKNOWN) {
                            append(hardware.computerSystem.firmware.name)
                            append(" ")
                        }
                        if (hardware.computerSystem.firmware.version != UNKNOWN) {
                            append(hardware.computerSystem.firmware.version)
                            append(" ")
                        }
                        if (hardware.computerSystem.firmware.description != UNKNOWN) {
                            append(hardware.computerSystem.firmware.description)
                            append(" ")
                        }
                        if (hardware.computerSystem.firmware.releaseDate != UNKNOWN) {
                            append(hardware.computerSystem.firmware.releaseDate)
                        }
                    }
                    .trim()
            val motherboard =
                buildString {
                        if (hardware.computerSystem.baseboard.manufacturer != UNKNOWN) {
                            append(hardware.computerSystem.baseboard.manufacturer)
                            append(" ")
                        }
                        if (hardware.computerSystem.baseboard.model != UNKNOWN) {
                            append(hardware.computerSystem.baseboard.model)
                            append(" ")
                        }
                        if (hardware.computerSystem.baseboard.version != UNKNOWN) {
                            append(hardware.computerSystem.baseboard.version)
                            append(" ")
                        }
                        if (hardware.computerSystem.baseboard.serialNumber != UNKNOWN) {
                            append(hardware.computerSystem.baseboard.serialNumber)
                        }
                    }
                    .trim()
            add(ItemValue.NameResource(Res.string.hardware_firmware, firmware))
            add(ItemValue.NameResource(Res.string.hardware_motherboard, motherboard))

            if (hardware.soundCards.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.sound_card, ""))
                hardware.soundCards.forEach { soundCard ->
                    add(ItemValue.Text(soundCard.name, soundCard.driverVersion))
                }
            }

            if (hardware.networkIFs.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.hardware_network_interfaces, ""))
                hardware.networkIFs.forEach { networkIF ->
                    val value =
                        buildString {
                                appendLine(networkIF.macaddr)
                                if (networkIF.iPv4addr.isNotEmpty()) {
                                    appendLine(networkIF.iPv4addr.joinToString { "\n" })
                                }
                                if (networkIF.iPv6addr.isNotEmpty()) {
                                    appendLine(networkIF.iPv6addr.joinToString { "\n" })
                                }
                            }
                            .trim()
                    add(ItemValue.Text(networkIF.name, value))
                }
            }

            if (hardware.powerSources.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.hardware_power_sources, ""))
                hardware.powerSources.forEach { powerSource ->
                    add(ItemValue.Text(powerSource.name, powerSource.deviceName))
                    if (powerSource.temperature != 0.0) {
                        add(
                            ItemValue.NameResource(
                                Res.string.temperature,
                                temperatureFormatter.format(powerSource.temperature.toFloat()),
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val UNKNOWN = "unknown"
    }
}
