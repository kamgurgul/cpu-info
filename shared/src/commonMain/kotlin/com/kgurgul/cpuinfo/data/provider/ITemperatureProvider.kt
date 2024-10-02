package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import kotlinx.coroutines.flow.Flow

interface ITemperatureProvider {

    val sensorsFlow: Flow<TemperatureItem>

    fun getBatteryTemperature(): Float?

    fun findCpuTemperatureLocation(): String?

    fun getCpuTemperature(path: String): Float?
}
