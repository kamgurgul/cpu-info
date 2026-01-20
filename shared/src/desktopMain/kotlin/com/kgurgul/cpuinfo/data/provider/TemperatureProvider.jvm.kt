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

    private val linuxTempReader = LinuxTemperatureReader()
    private val macOSTempReader = MacOSTemperatureReader()
    private val windowsTempReader by lazy { WindowsTemperatureReader(systemInfo) }

    actual override val sensorsFlow: Flow<TemperatureItem> = flow {
        while (true) {
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

    actual override fun findCpuTemperatureLocation(): String? = ""

    actual override fun getCpuTemperature(path: String): Float? {
        val cpuTemp = systemInfo.hardware.sensors.cpuTemperature
        return if (cpuTemp.isNaN() || cpuTemp == 0.0) null else cpuTemp.toFloat()
    }

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

    private val cpuTempReader = WindowsCpuTemperatureReader()
    private val diskTempReader = WindowsDiskTemperatureReader()

    fun readTemperatures(): List<TemperatureItem> {
        val sensors = mutableListOf<TemperatureItem>()
        var sensorId = SENSOR_ID_START

        // 1. Try direct CPU temperature reading
        sensorId = cpuTempReader.readCpuTemperatures(sensors, sensorId)

        // 2. Try OSHI hardware sensors
        sensorId = readOshiSensors(sensors, sensorId)

        // 3. Try WMI thermal zones (MSAcpi_ThermalZoneTemperature)
        sensorId = readWmiThermalZones(sensors, sensorId)

        // 4. Try performance counter thermal zones
        sensorId = readPerfCounterThermalZones(sensors, sensorId)

        // 5. Try disk temperatures
        sensorId = diskTempReader.readDiskTemperatures(sensors, sensorId)

        // 6. Try LibreHardwareMonitor/OpenHardwareMonitor WMI if running
        sensorId = readHardwareMonitorWmi(sensors, sensorId)

        // 7. Try battery temperature
        readBatteryTemperature(sensors, sensorId)

        return sensors.distinctBy { it.name }
    }

    private fun readOshiSensors(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val cpuTemp = systemInfo.hardware.sensors.cpuTemperature
            if (!cpuTemp.isNaN() && cpuTemp > 0 && isTemperatureValid(cpuTemp.toFloat())) {
                sensors.add(createTemperatureItem(sensorId++, "CPU (OSHI)", cpuTemp.toFloat()))
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readWmiThermalZones(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        val command =
            listOf(
                "powershell",
                "-Command",
                "Get-WmiObject MSAcpi_ThermalZoneTemperature -Namespace root/wmi 2>\$null | " +
                    "Select-Object InstanceName, CurrentTemperature | " +
                    "ForEach-Object { Write-Output (\$_.InstanceName + '|' + \$_.CurrentTemperature) }",
            )
        runCommandWithLines(command) { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                val name =
                    parts[0].trim().replace("ACPI\\ThermalZone\\", "").replace("_", " ").ifEmpty {
                        "Thermal Zone"
                    }
                parts[1].trim().toDoubleOrNull()?.let { rawTemp ->
                    val tempCelsius = (rawTemp / 10.0 - 273.15).toFloat()
                    if (isTemperatureValid(tempCelsius)) {
                        sensors.add(createTemperatureItem(sensorId++, name, tempCelsius))
                    }
                }
            }
        }
        return sensorId
    }

    private fun readPerfCounterThermalZones(
        sensors: MutableList<TemperatureItem>,
        startId: Int,
    ): Int {
        var sensorId = startId
        val command =
            listOf(
                "powershell",
                "-Command",
                "Get-WmiObject Win32_PerfFormattedData_Counters_ThermalZoneInformation 2>\$null | " +
                    "Select-Object Name, Temperature | " +
                    "ForEach-Object { Write-Output (\$_.Name + '|' + \$_.Temperature) }",
            )
        runCommandWithLines(command) { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                val name = parts[0].trim().ifEmpty { "Thermal Zone" }
                parts[1].trim().toDoubleOrNull()?.let { rawTemp ->
                    val tempCelsius = (rawTemp - 273.15).toFloat()
                    if (isTemperatureValid(tempCelsius)) {
                        sensors.add(createTemperatureItem(sensorId++, name, tempCelsius))
                    }
                }
            }
        }
        return sensorId
    }

    private fun readHardwareMonitorWmi(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId

        // Try OpenHardwareMonitor namespace
        val ohmCommand =
            listOf(
                "powershell",
                "-Command",
                "Get-WmiObject -Namespace root\\OpenHardwareMonitor -Class Sensor 2>\$null | " +
                    "Where-Object { \$_.SensorType -eq 'Temperature' } | " +
                    "Select-Object Name, Value | " +
                    "ForEach-Object { Write-Output (\$_.Name + '|' + \$_.Value) }",
            )
        runCommandWithLines(ohmCommand) { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                val name = parts[0].trim()
                parts[1].trim().toFloatOrNull()?.let { temp ->
                    if (isTemperatureValid(temp)) {
                        sensors.add(createTemperatureItem(sensorId++, name, temp))
                    }
                }
            }
        }

        // Try LibreHardwareMonitor namespace
        val lhmCommand =
            listOf(
                "powershell",
                "-Command",
                "Get-WmiObject -Namespace root\\LibreHardwareMonitor -Class Sensor 2>\$null | " +
                    "Where-Object { \$_.SensorType -eq 'Temperature' } | " +
                    "Select-Object Name, Value | " +
                    "ForEach-Object { Write-Output (\$_.Name + '|' + \$_.Value) }",
            )
        runCommandWithLines(lhmCommand) { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                val name = parts[0].trim()
                parts[1].trim().toFloatOrNull()?.let { temp ->
                    if (isTemperatureValid(temp)) {
                        sensors.add(createTemperatureItem(sensorId++, name, temp))
                    }
                }
            }
        }

        return sensorId
    }

    private fun readBatteryTemperature(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        val command =
            listOf(
                "powershell",
                "-Command",
                "Get-WmiObject -Namespace root\\wmi -Class BatteryStatus 2>\$null | " +
                    "Select-Object Temperature | " +
                    "ForEach-Object { Write-Output \$_.Temperature }",
            )
        runCommandWithLines(command) { line ->
            line.trim().toDoubleOrNull()?.let { rawTemp ->
                if (rawTemp > 0) {
                    val tempCelsius = (rawTemp / 10.0 - 273.15).toFloat()
                    if (isTemperatureValid(tempCelsius)) {
                        sensors.add(createTemperatureItem(sensorId++, "Battery", tempCelsius))
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

// =============================================================================
// Windows CPU Temperature Reader
// =============================================================================

private class WindowsCpuTemperatureReader : BaseTemperatureReader() {

    fun readCpuTemperatures(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId

        // Try reading via multiple WMI methods
        sensorId = readViaPerfCounters(sensors, sensorId)
        sensorId = readViaMsAcpiThermal(sensors, sensorId)

        return sensorId
    }

    private fun readViaPerfCounters(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    """
                    try {
                        ${'$'}temps = Get-CimInstance -Namespace root\cimv2 -ClassName Win32_PerfFormattedData_Counters_ThermalZoneInformation -ErrorAction SilentlyContinue
                        if (${'$'}temps) {
                            ${'$'}temps | ForEach-Object {
                                ${'$'}kelvin = ${'$'}_.Temperature
                                if (${'$'}kelvin -gt 0) {
                                    ${'$'}celsius = ${'$'}kelvin - 273.15
                                    Write-Output ("CPU|" + [math]::Round(${'$'}celsius, 1))
                                }
                            }
                        }
                    } catch {}
                    """
                        .trimIndent(),
                )
            runCommandWithLines(command) { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    parts[1].trim().toFloatOrNull()?.let { temp ->
                        if (isTemperatureValid(temp)) {
                            sensors.add(createTemperatureItem(sensorId++, "CPU Core", temp))
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readViaMsAcpiThermal(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    """
                    ${'$'}thermal = Get-CimInstance -Namespace root\wmi -ClassName MSAcpi_ThermalZoneTemperature -ErrorAction SilentlyContinue
                    if (${'$'}thermal) {
                        ${'$'}thermal | ForEach-Object {
                            ${'$'}name = ${'$'}_.InstanceName -replace 'ACPI\\ThermalZone\\','' -replace '_',' '
                            ${'$'}temp = (${'$'}_.CurrentTemperature / 10) - 273.15
                            Write-Output (${'$'}name + "|" + [math]::Round(${'$'}temp, 1))
                        }
                    }
                    """
                        .trimIndent(),
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
        } catch (_: Exception) {}
        return sensorId
    }
}

// =============================================================================
// Windows Disk Temperature Reader
// =============================================================================

private class WindowsDiskTemperatureReader : BaseTemperatureReader() {

    fun readDiskTemperatures(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId

        // Read NVMe temperatures
        sensorId = readNvmeTemperatures(sensors, sensorId)

        // Read SATA temperatures
        sensorId = readSataTemperatures(sensors, sensorId)

        // Fallback: generic disk temperatures
        sensorId = readGenericDiskTemperatures(sensors, sensorId)

        return sensorId
    }

    private fun readNvmeTemperatures(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    """
                    ${'$'}disks = Get-PhysicalDisk | Where-Object { ${'$'}_.BusType -eq 'NVMe' }
                    foreach (${'$'}disk in ${'$'}disks) {
                        try {
                            ${'$'}smart = ${'$'}disk | Get-StorageReliabilityCounter -ErrorAction SilentlyContinue
                            if (${'$'}smart -and ${'$'}smart.Temperature) {
                                Write-Output ("NVMe " + ${'$'}disk.DeviceId + " (" + ${'$'}disk.FriendlyName.Trim() + ")|" + ${'$'}smart.Temperature)
                            }
                        } catch {}
                    }
                    """
                        .trimIndent(),
                )
            runCommandWithLines(command) { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    val name = parts[0].trim()
                    parts[1].trim().toFloatOrNull()?.let { temp ->
                        if (isTemperatureValid(temp)) {
                            sensors.add(createTemperatureItem(sensorId++, name, temp))
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readSataTemperatures(sensors: MutableList<TemperatureItem>, startId: Int): Int {
        var sensorId = startId
        try {
            val command =
                listOf(
                    "powershell",
                    "-Command",
                    """
                    ${'$'}disks = Get-PhysicalDisk | Where-Object { ${'$'}_.BusType -eq 'SATA' -or ${'$'}_.BusType -eq 'ATA' }
                    foreach (${'$'}disk in ${'$'}disks) {
                        try {
                            ${'$'}smart = ${'$'}disk | Get-StorageReliabilityCounter -ErrorAction SilentlyContinue
                            if (${'$'}smart -and ${'$'}smart.Temperature) {
                                Write-Output ("SATA " + ${'$'}disk.DeviceId + " (" + ${'$'}disk.FriendlyName.Trim() + ")|" + ${'$'}smart.Temperature)
                            }
                        } catch {}
                    }
                    """
                        .trimIndent(),
                )
            runCommandWithLines(command) { line ->
                val parts = line.split("|")
                if (parts.size == 2) {
                    val name = parts[0].trim()
                    parts[1].trim().toFloatOrNull()?.let { temp ->
                        if (isTemperatureValid(temp)) {
                            sensors.add(createTemperatureItem(sensorId++, name, temp))
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return sensorId
    }

    private fun readGenericDiskTemperatures(
        sensors: MutableList<TemperatureItem>,
        startId: Int,
    ): Int {
        var sensorId = startId
        val command =
            listOf(
                "powershell",
                "-Command",
                "Get-PhysicalDisk | Get-StorageReliabilityCounter 2>\$null | " +
                    "Select-Object DeviceId, Temperature | " +
                    "ForEach-Object { Write-Output ('Disk ' + \$_.DeviceId + '|' + \$_.Temperature) }",
            )
        runCommandWithLines(command) { line ->
            val parts = line.split("|")
            if (parts.size == 2) {
                val name = parts[0].trim()
                parts[1].trim().toFloatOrNull()?.let { temp ->
                    if (
                        isTemperatureValid(temp) &&
                            !sensors.any {
                                it.name.let { n ->
                                    n is TextResource.Text && n.value.contains("Disk")
                                }
                            }
                    ) {
                        sensors.add(createTemperatureItem(sensorId++, name, temp))
                    }
                }
            }
        }
        return sensorId
    }
}
