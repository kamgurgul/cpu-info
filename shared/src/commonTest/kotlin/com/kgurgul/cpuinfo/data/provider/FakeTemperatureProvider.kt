package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import kotlinx.coroutines.flow.Flow

class FakeTemperatureProvider(
    override val sensorsFlow: Flow<TemperatureItem>,
    var batteryTemp: Float? = null,
    var cpuTempLocation: String? = null,
    var cpuTemp: Float?,
) : ITemperatureProvider {

    override fun getBatteryTemperature(): Float? {
        return batteryTemp
    }

    override fun findCpuTemperatureLocation(): String? {
        return cpuTempLocation
    }

    override fun getCpuTemperature(path: String): Float? {
        return cpuTemp
    }
}
