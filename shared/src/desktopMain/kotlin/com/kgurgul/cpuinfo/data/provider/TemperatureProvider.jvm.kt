/*
 * Copyright 2017 KG Soft
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class TemperatureProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual val sensorsFlow: Flow<TemperatureItem> = emptyFlow()

    actual fun getBatteryTemperature(): Float? {
        return systemInfo.hardware.powerSources
            .find { it.temperature != 0.0 }
            ?.temperature
            ?.toFloat()
    }

    actual fun findCpuTemperatureLocation(): String? {
        return ""
    }

    actual fun getCpuTemp(path: String): Float? {
        val cpuTemp = systemInfo.hardware.sensors.cpuTemperature
        return if (cpuTemp.isNaN() || cpuTemp == 0.0) {
            null
        } else {
            cpuTemp.toFloat()
        }
    }
}