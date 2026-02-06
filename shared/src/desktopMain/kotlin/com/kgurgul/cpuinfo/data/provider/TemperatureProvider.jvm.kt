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
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.utils.round1
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class TemperatureProvider actual constructor() : KoinComponent, ITemperatureProvider {

    private val systemInfo: SystemInfo by inject()
    private val platformInfo = PlatformInfo()

    private val linuxTempReader by lazy { LinuxTemperatureReader() }
    private val macOSTempReader by lazy { MacOSTemperatureReader() }
    private val windowsTempReader by lazy { WindowsTemperatureReader(systemInfo) }

    actual override val sensorsFlow: Flow<TemperatureItem> = flow {
        while (true) {
            // Emit battery temperature if available
            getBatteryTemperature()?.let {
                emit(
                    TemperatureItem(
                        id = ID_BATTERY,
                        icon = Res.drawable.ic_battery,
                        name = TextResource.Resource(Res.string.battery),
                        temperature = it,
                    )
                )
            }

            // Emit platform-specific sensor temperatures
            val sensors =
                when {
                    platformInfo.isLinux -> linuxTempReader.readTemperatures()
                    platformInfo.isMacOS -> macOSTempReader.readTemperatures()
                    platformInfo.isWindows -> windowsTempReader.readTemperatures()
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

    actual override fun findCpuTemperatureLocation(): String? = null

    actual override fun getCpuTemperature(path: String): Float? = null

    actual override suspend fun isAdminRequired(): Boolean {
        return platformInfo.isWindows && !checkIsRunningAsAdmin()
    }

    private suspend fun checkIsRunningAsAdmin(): Boolean =
        withContext(Dispatchers.IO) {
            if (!platformInfo.isWindows) {
                false
            } else {
                try {
                    val process = ProcessBuilder("fltMC").redirectErrorStream(true).start()
                    process.inputStream.bufferedReader().use { it.readText() }
                    process.waitFor() == 0
                } catch (_: Exception) {
                    false
                }
            }
        }

    companion object {
        private const val REFRESH_DELAY = 3000L
        private const val ID_BATTERY = -1
    }
}

// =============================================================================
// Platform Detection
// =============================================================================

private class PlatformInfo {
    private val osName = System.getProperty("os.name").lowercase()
    val isLinux = osName.contains("linux")
    val isMacOS = osName.contains("mac") || osName.contains("darwin")
    val isWindows = osName.contains("windows")
}

// =============================================================================
// Base Temperature Reader
// =============================================================================

private abstract class BaseTemperatureReader {

    protected fun isTemperatureValid(temp: Float): Boolean = temp in -50f..250f

    protected fun createTemperatureItem(
        id: Int,
        name: String,
        temperature: Float,
    ): TemperatureItem =
        TemperatureItem(
            id = id,
            icon = Res.drawable.baseline_thermostat_24,
            name = TextResource.Text(name),
            temperature = temperature.round1(),
        )

    protected fun readFileContent(file: File): String? {
        return try {
            if (file.exists() && file.canRead()) file.readText() else null
        } catch (_: Exception) {
            null
        }
    }

    protected fun runCommand(vararg command: String): String? {
        return try {
            val process = ProcessBuilder(*command).redirectErrorStream(true).start()
            process.inputStream.bufferedReader().readText()
        } catch (_: Exception) {
            null
        }
    }

    protected fun runCommandWithLines(command: List<String>, action: (String) -> Unit) {
        try {
            val process = ProcessBuilder(command).redirectErrorStream(true).start()
            BufferedReader(InputStreamReader(process.inputStream)).forEachLine { action(it) }
        } catch (_: Exception) {
            // Ignore errors
        }
    }
}

// =============================================================================
// Linux Temperature Reader
// =============================================================================

private class LinuxTemperatureReader : BaseTemperatureReader() {

    fun readTemperatures(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        var sensorId = SENSOR_ID_START

        sensorId = readHwmonSensors(sensors, sensorId)
        readThermalZones(sensors, sensorId)

        return sensors
    }

    private fun readHwmonSensors(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        val hwmonDir = File("/sys/class/hwmon")
        if (!hwmonDir.exists()) return sensorId

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
                            sensors.add(createTemperatureItem(sensorId++, label, temp))
                        }
                    }
                }
        }
        return sensorId
    }

    private fun readThermalZones(sensors: MutableList<TemperatureItem>, startId: Int) {
        var sensorId = startId
        val thermalDir = File("/sys/class/thermal")
        if (!thermalDir.exists()) return

        thermalDir
            .listFiles { file -> file.name.startsWith("thermal_zone") }
            ?.forEach { zone ->
                val typeFile = File(zone, "type")
                val tempFile = File(zone, "temp")
                val type = readFileContent(typeFile)?.trim() ?: zone.name

                readFileContent(tempFile)?.trim()?.toDoubleOrNull()?.let { milliDegrees ->
                    val temp = (milliDegrees / 1000.0).toFloat()
                    if (isTemperatureValid(temp)) {
                        sensors.add(createTemperatureItem(sensorId++, type, temp))
                    }
                }
            }
    }

    companion object {
        private const val SENSOR_ID_START = 100
    }
}

// =============================================================================
// macOS Temperature Reader
// =============================================================================

private class MacOSTemperatureReader : BaseTemperatureReader() {

    fun readTemperatures(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        var sensorId = SENSOR_ID_START

        sensorId = readIOHWSensors(sensors, sensorId)
        sensorId = readSmartBatteryTemp(sensors, sensorId)
        readGenericThermalData(sensors, sensorId)

        return sensors.distinctBy { it.name }
    }

    private fun readIOHWSensors(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val output = runCommand("ioreg", "-r", "-c", "IOHWSensor") ?: return sensorId
            val sensorPattern =
                Regex(
                    "\"IOHWSensorLocation\"\\s*=\\s*\"([^\"]+)\"[\\s\\S]*?" +
                        "\"CurrentValue\"\\s*=\\s*(\\d+)"
                )
            sensorPattern.findAll(output).forEach { match ->
                val location = match.groupValues[1]
                val rawValue = match.groupValues[2].toLongOrNull() ?: return@forEach
                val temp = (rawValue / 65536.0).toFloat()
                if (isTemperatureValid(temp)) {
                    sensors.add(createTemperatureItem(sensorId++, location, temp))
                }
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readSmartBatteryTemp(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val output = runCommand("ioreg", "-r", "-c", "AppleSmartBattery") ?: return sensorId
            val tempPattern = Regex("\"Temperature\"\\s*=\\s*(\\d+)")
            tempPattern.find(output)?.let { match ->
                val rawTemp = match.groupValues[1].toIntOrNull()
                if (rawTemp != null) {
                    val tempCelsius = (rawTemp / 10.0 - 273.15).toFloat()
                    if (isTemperatureValid(tempCelsius)) {
                        sensors.add(createTemperatureItem(sensorId++, "Battery", tempCelsius))
                    }
                }
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readGenericThermalData(sensors: MutableList<TemperatureItem>, startId: Int) {
        var sensorId = startId
        try {
            val output =
                runCommand("sh", "-c", "ioreg -l | grep -i 'temperature\\|thermal'") ?: return
            val genericTempPattern =
                Regex(
                    "\"([^\"]*[Tt]emp[^\"]*|[^\"]*[Tt]hermal[^\"]*Location[^\"]*)\"" +
                        "\\s*=\\s*(?:\"([^\"]+)\"|([\\d.]+))"
                )
            genericTempPattern.findAll(output).take(10).forEach { match ->
                val name = match.groupValues[1]
                val value = match.groupValues[3].toFloatOrNull()
                if (value != null && isTemperatureValid(value) && value > 1) {
                    sensors.add(createTemperatureItem(sensorId++, name, value))
                }
            }
        } catch (_: Exception) {}
    }

    companion object {
        private const val SENSOR_ID_START = 200
    }
}

// =============================================================================
// Windows Temperature Reader
// =============================================================================

private class WindowsTemperatureReader(private val systemInfo: SystemInfo) :
    BaseTemperatureReader() {

    fun readTemperatures(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        var sensorId = SENSOR_ID_START

        // 1. Try OSHI hardware sensors (works with some hardware)
        sensorId = readOshiSensors(sensors, sensorId)

        // 2. Try disk temperatures (most reliable on Windows)
        sensorId = readDiskTemperatures(sensors, sensorId)

        // 3. Try LibreHardwareMonitor/OpenHardwareMonitor WMI if running
        sensorId = readHardwareMonitorWmi(sensors, sensorId)

        // 4. Try WMI thermal zones (requires admin)
        sensorId = readWmiThermalZones(sensors, sensorId)

        return sensors.distinctBy { it.name }
    }

    private fun readOshiSensors(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val cpuTemp = systemInfo.hardware.sensors.cpuTemperature
            if (!cpuTemp.isNaN() && cpuTemp > 0 && isTemperatureValid(cpuTemp.toFloat())) {
                sensors.add(createTemperatureItem(sensorId++, "CPU", cpuTemp.toFloat()))
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readDiskTemperatures(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        val existingDiskIds = mutableSetOf<String>()

        // Use a simple, reliable single-line command
        val command =
            listOf(
                "powershell",
                "-NoProfile",
                "-Command",
                $$"Get-PhysicalDisk | ForEach-Object { $d=$_; $s=$d|Get-StorageReliabilityCounter -ErrorAction SilentlyContinue; if($s -and $s.Temperature -gt 0){ Write-Output ($d.DeviceId+'|'+$d.FriendlyName+'|'+$d.BusType+'|'+$s.Temperature) } }",
            )

        runCommandWithLines(command) { line ->
            val parts = line.split("|")
            if (parts.size >= 4) {
                val deviceId = parts[0].trim()
                val friendlyName = parts[1].trim()
                val busType = parts[2].trim()
                val temp = parts[3].trim().toFloatOrNull()

                if (temp != null && isTemperatureValid(temp) && deviceId !in existingDiskIds) {
                    existingDiskIds.add(deviceId)
                    val prefix =
                        when {
                            busType.contains("NVMe", ignoreCase = true) -> "NVMe"
                            busType.contains("SATA", ignoreCase = true) ||
                                busType.contains("ATA", ignoreCase = true) -> "SATA"
                            else -> "Disk"
                        }
                    val name = "$prefix $deviceId ($friendlyName)"
                    sensors.add(createTemperatureItem(sensorId++, name, temp))
                }
            }
        }
        return sensorId
    }

    private fun readWmiThermalZones(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        val command =
            listOf(
                "powershell",
                "-NoProfile",
                "-Command",
                $$"Get-CimInstance -Namespace root/wmi -ClassName MSAcpi_ThermalZoneTemperature -ErrorAction SilentlyContinue | ForEach-Object { $n=$_.InstanceName -replace 'ACPI\\\\ThermalZone\\\\','' -replace '_',' '; $t=($_.CurrentTemperature/10)-273.15; Write-Output ($n+'|'+[math]::Round($t,1)) }",
            )
        runCommandWithLines(command) { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                val name = parts[0].trim().ifEmpty { "Thermal Zone" }
                parts[1].trim().toFloatOrNull()?.let { temp ->
                    if (
                        isTemperatureValid(temp) &&
                            !sensors.any { it.name == TextResource.Text(name) }
                    ) {
                        sensors.add(createTemperatureItem(sensorId++, name, temp))
                    }
                }
            }
        }
        return sensorId
    }

    private fun readHardwareMonitorWmi(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId

        // Try both OpenHardwareMonitor and LibreHardwareMonitor namespaces
        listOf("OpenHardwareMonitor", "LibreHardwareMonitor").forEach { namespace ->
            val command =
                listOf(
                    "powershell",
                    "-NoProfile",
                    "-Command",
                    $$"Get-CimInstance -Namespace root/$$namespace -ClassName Sensor -ErrorAction SilentlyContinue | Where-Object { $_.SensorType -eq 'Temperature' } | ForEach-Object { Write-Output ($_.Name+'|'+$_.Value) }",
                )
            runCommandWithLines(command) { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    val name = parts[0].trim()
                    parts[1].trim().toFloatOrNull()?.let { temp ->
                        if (isTemperatureValid(temp) && name.isNotEmpty()) {
                            sensors.add(createTemperatureItem(sensorId++, name, temp))
                        }
                    }
                }
            }
        }

        return sensorId
    }

    companion object {
        private const val SENSOR_ID_START = 300
    }
}
