package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import kotlinx.coroutines.flow.Flow

expect class TemperatureProvider() : ITemperatureProvider {

    override val sensorsFlow: Flow<TemperatureItem>

    override fun getBatteryTemperature(): Float?

    override fun findCpuTemperatureLocation(): String?

    override fun getCpuTemperature(path: String): Float?
}
