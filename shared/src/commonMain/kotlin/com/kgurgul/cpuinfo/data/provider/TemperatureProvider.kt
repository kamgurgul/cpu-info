package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
expect class TemperatureProvider() : ITemperatureProvider {

    override val sensorsFlow: Flow<TemperatureItem>

    override fun getBatteryTemperature(): Float?

    override fun findCpuTemperatureLocation(): String?

    override fun getCpuTemperature(path: String): Float?
}
