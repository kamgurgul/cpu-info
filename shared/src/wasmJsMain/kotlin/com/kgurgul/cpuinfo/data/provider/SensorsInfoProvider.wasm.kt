package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.SensorData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.component.KoinComponent

actual class SensorsInfoProvider actual constructor() : KoinComponent {

    actual fun getSensorData(): Flow<List<SensorData>> = emptyFlow()

    companion object {
        private const val REFRESH_DELAY = 100L
    }
}
