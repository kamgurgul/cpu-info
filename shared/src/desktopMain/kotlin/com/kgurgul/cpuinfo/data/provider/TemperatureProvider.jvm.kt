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

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_thermostat_24
import com.kgurgul.cpuinfo.utils.round1
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class TemperatureProvider actual constructor() : KoinComponent, ITemperatureProvider {

    private val systemInfo: SystemInfo by inject()
    private val osName = System.getProperty("os.name").lowercase()
    private val isLinux = osName.contains("linux")
    private val isMacOS = osName.contains("mac") || osName.contains("darwin")
    private val isWindows = osName.contains("windows")

    actual override val sensorsFlow: Flow<TemperatureItem> = flow {
        while (true) {
            val sensors =
                when {
                    isLinux -> getLinuxTemperatureSensors()
                    isMacOS -> getMacOSTemperatureSensors()
                    isWindows -> getWindowsTemperatureSensors()
                    else -> emptyList()
                }
            sensors.forEach { emit(it) }
            delay(REFRESH_DELAY)
        }
    }

    actual override fun getBatteryTemperature(): Float? {
        return systemInfo.hardware.powerSources
            .find { it.temperature != 0.0 }
            ?.temperature
            ?.toFloat()
    }

    actual override fun findCpuTemperatureLocation(): String? {
        return ""
    }

    actual override fun getCpuTemperature(path: String): Float? {
        val cpuTemp = systemInfo.hardware.sensors.cpuTemperature
        return if (cpuTemp.isNaN() || cpuTemp == 0.0) {
            null
        } else {
            cpuTemp.toFloat()
        }
    }

    /**
     * Linux: Read temperature sensors from /sys/class/hwmon/ Each hwmon directory contains
     * temp*_input (millidegrees) and temp*_label files
     */
    private fun getLinuxTemperatureSensors(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        val hwmonDir = File("/sys/class/hwmon")
        if (!hwmonDir.exists()) return sensors

        var sensorId = LINUX_SENSOR_ID_START
        hwmonDir.listFiles()?.forEach { hwmon ->
            val hwmonName = readFileContent(File(hwmon, "name"))?.trim() ?: hwmon.name

            hwmon
                .listFiles { file -> file.name.matches(Regex("temp\\d+_input")) }
                ?.forEach { tempFile ->
                    val tempId = tempFile.name.removePrefix("temp").removeSuffix("_input")
                    val labelFile = File(hwmon, "temp${tempId}_label")
                    val label = readFileContent(labelFile)?.trim() ?: "$hwmonName temp$tempId"

                    readFileContent(tempFile)?.trim()?.toDoubleOrNull()?.let { milliDegrees ->
                        val temp = (milliDegrees / 1000.0).toFloat()
                        if (isTemperatureValid(temp)) {
                            sensors.add(
                                TemperatureItem(
                                    id = sensorId++,
                                    icon = Res.drawable.baseline_thermostat_24,
                                    name = TextResource.Text(label),
                                    temperature = temp.round1(),
                                )
                            )
                        }
                    }
                }
        }
        return sensors
    }

    /**
     * macOS: Read temperature sensors using ioreg and powermetrics Uses SMC (System Management
     * Controller) for hardware temperatures
     */
    private fun getMacOSTemperatureSensors(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        var sensorId = MACOS_SENSOR_ID_START

        // Try thermal zones from IOHIDSystem or AppleHWSensor
        try {
            val process =
                ProcessBuilder("ioreg", "-r", "-c", "IOHWSensor").redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readText()

            // Parse IOHWSensor output for temperature sensors
            val sensorPattern =
                Regex(
                    "\"IOHWSensorLocation\"\\s*=\\s*\"([^\"]+)\"[\\s\\S]*?" +
                        "\"CurrentValue\"\\s*=\\s*(\\d+)"
                )
            sensorPattern.findAll(output).forEach { match ->
                val location = match.groupValues[1]
                val rawValue = match.groupValues[2].toLongOrNull() ?: return@forEach
                // IOHWSensor values are typically in a fixed-point format
                val temp = (rawValue / 65536.0).toFloat()
                if (isTemperatureValid(temp)) {
                    sensors.add(
                        TemperatureItem(
                            id = sensorId++,
                            icon = Res.drawable.baseline_thermostat_24,
                            name = TextResource.Text(location),
                            temperature = temp.round1(),
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Ignore errors
        }

        // Try AppleSmartBattery for additional thermal info
        try {
            val process =
                ProcessBuilder("ioreg", "-r", "-c", "AppleSmartBattery")
                    .redirectErrorStream(true)
                    .start()
            val output = process.inputStream.bufferedReader().readText()

            val tempPattern = Regex("\"Temperature\"\\s*=\\s*(\\d+)")
            tempPattern.find(output)?.let { match ->
                val rawTemp = match.groupValues[1].toIntOrNull()
                if (rawTemp != null) {
                    // Battery temperature is in 0.1K units
                    val tempCelsius = (rawTemp / 10.0 - 273.15).toFloat()
                    if (isTemperatureValid(tempCelsius)) {
                        sensors.add(
                            TemperatureItem(
                                id = sensorId++,
                                icon = Res.drawable.baseline_thermostat_24,
                                name = TextResource.Text("Battery"),
                                temperature = tempCelsius.round1(),
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore errors
        }

        // Try reading thermal zones
        try {
            val process =
                ProcessBuilder("sh", "-c", "ioreg -l | grep -i 'temperature\\|thermal'")
                    .redirectErrorStream(true)
                    .start()
            val output = process.inputStream.bufferedReader().readText()

            // Parse generic temperature readings
            val genericTempPattern =
                Regex(
                    "\"([^\"]*[Tt]emp[^\"]*|[^\"]*[Tt]hermal[^\"]*Location[^\"]*)\"" +
                        "\\s*=\\s*(?:\"([^\"]+)\"|([\\d.]+))"
                )
            genericTempPattern.findAll(output).take(10).forEach { match ->
                val name = match.groupValues[1]
                val value = match.groupValues[3].toFloatOrNull()
                if (value != null && isTemperatureValid(value) && value > 1) {
                    sensors.add(
                        TemperatureItem(
                            id = sensorId++,
                            icon = Res.drawable.baseline_thermostat_24,
                            name = TextResource.Text(name),
                            temperature = value.round1(),
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Ignore errors
        }

        return sensors.distinctBy { it.name }
    }

    /**
     * Windows: Read temperature sensors using WMI via PowerShell Queries
     * MSAcpi_ThermalZoneTemperature and Win32_TemperatureProbe
     */
    private fun getWindowsTemperatureSensors(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        var sensorId = WINDOWS_SENSOR_ID_START

        // Query MSAcpi_ThermalZoneTemperature for thermal zones
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    "Get-WmiObject MSAcpi_ThermalZoneTemperature -Namespace root/wmi " +
                        "2>\$null | Select-Object InstanceName, CurrentTemperature | " +
                        "ForEach-Object { Write-Output (\"\" + \$_.InstanceName + \"|\" + " +
                        "\$_.CurrentTemperature) }",
                )
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    val name =
                        parts[0]
                            .trim()
                            .replace("ACPI\\ThermalZone\\", "")
                            .replace("_", " ")
                            .ifEmpty { "Thermal Zone" }
                    val rawTemp = parts[1].trim().toDoubleOrNull()
                    if (rawTemp != null) {
                        // WMI temperature is in tenths of Kelvin
                        val tempCelsius = (rawTemp / 10.0 - 273.15).toFloat()
                        if (isTemperatureValid(tempCelsius)) {
                            sensors.add(
                                TemperatureItem(
                                    id = sensorId++,
                                    icon = Res.drawable.baseline_thermostat_24,
                                    name = TextResource.Text(name),
                                    temperature = tempCelsius.round1(),
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore errors
        }

        // Query Win32_PerfFormattedData_Counters_ThermalZoneInformation
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    "Get-WmiObject Win32_PerfFormattedData_Counters_ThermalZoneInformation " +
                        "2>\$null | Select-Object Name, Temperature | " +
                        "ForEach-Object { Write-Output (\"\" + \$_.Name + \"|\" + \$_.Temperature) }",
                )
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    val name = parts[0].trim().ifEmpty { "Thermal Zone" }
                    val rawTemp = parts[1].trim().toDoubleOrNull()
                    if (rawTemp != null) {
                        // This class reports in Kelvin
                        val tempCelsius = (rawTemp - 273.15).toFloat()
                        if (isTemperatureValid(tempCelsius)) {
                            sensors.add(
                                TemperatureItem(
                                    id = sensorId++,
                                    icon = Res.drawable.baseline_thermostat_24,
                                    name = TextResource.Text(name),
                                    temperature = tempCelsius.round1(),
                                )
                            )
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore errors
        }

        // Query disk drive temperatures if available
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    "Get-PhysicalDisk | Get-StorageReliabilityCounter 2>\$null | " +
                        "Select-Object DeviceId, Temperature | " +
                        "ForEach-Object { Write-Output (\"Disk \" + \$_.DeviceId + \"|\" + " +
                        "\$_.Temperature) }",
                )
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    val name = parts[0].trim()
                    val temp = parts[1].trim().toFloatOrNull()
                    if (temp != null && isTemperatureValid(temp)) {
                        sensors.add(
                            TemperatureItem(
                                id = sensorId++,
                                icon = Res.drawable.baseline_thermostat_24,
                                name = TextResource.Text(name),
                                temperature = temp.round1(),
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore errors
        }

        return sensors.distinctBy { it.name }
    }

    private fun readFileContent(file: File): String? {
        return try {
            if (file.exists() && file.canRead()) {
                file.readText()
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private fun isTemperatureValid(temp: Float): Boolean = temp in -50f..250f

    companion object {
        private const val REFRESH_DELAY = 3000L
        private const val LINUX_SENSOR_ID_START = 100
        private const val MACOS_SENSOR_ID_START = 200
        private const val WINDOWS_SENSOR_ID_START = 300
    }
}
