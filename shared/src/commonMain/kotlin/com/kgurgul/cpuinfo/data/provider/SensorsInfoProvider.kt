package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.SensorData
import kotlinx.coroutines.flow.Flow

expect class SensorsInfoProvider() {

    fun getSensorData(): Flow<List<SensorData>>
}
