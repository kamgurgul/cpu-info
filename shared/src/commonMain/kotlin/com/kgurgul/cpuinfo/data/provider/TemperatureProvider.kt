package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
expect class TemperatureProvider() {

    val sensorsFlow: Flow<TemperatureItem>

    fun getBatteryTemperature(): Float?

    fun findCpuTemperatureLocation(): String?

    fun getCpuTemp(path: String): Float?
}