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
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.temp_thermal_state
import com.kgurgul.cpuinfo.shared.temp_thermal_state_critical
import com.kgurgul.cpuinfo.shared.temp_thermal_state_fair
import com.kgurgul.cpuinfo.shared.temp_thermal_state_nominal
import com.kgurgul.cpuinfo.shared.temp_thermal_state_serious
import com.kgurgul.cpuinfo.shared.unknown
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSProcessInfoThermalState
import platform.Foundation.thermalState

@Factory
actual class TemperatureProvider actual constructor() {

    actual val sensorsFlow: Flow<TemperatureItem> = flow {
        val thermalState = when (NSProcessInfo.processInfo.thermalState) {
            NSProcessInfoThermalState.NSProcessInfoThermalStateNominal ->
                getString(Res.string.temp_thermal_state_nominal)

            NSProcessInfoThermalState.NSProcessInfoThermalStateFair ->
                getString(Res.string.temp_thermal_state_fair)

            NSProcessInfoThermalState.NSProcessInfoThermalStateSerious ->
                getString(Res.string.temp_thermal_state_serious)

            NSProcessInfoThermalState.NSProcessInfoThermalStateCritical ->
                getString(Res.string.temp_thermal_state_critical)
            else -> getString(Res.string.unknown)
        }
        emit(
            TemperatureItem(
                id = ID_THERMAL_STATE,
                icon = Res.drawable.ic_temperature,
                name = getString(Res.string.temp_thermal_state, thermalState),
                temperature = Float.NaN,
            )
        )
        delay(REFRESH_DELAY)
    }

    actual fun getBatteryTemperature(): Float? {
        return null
    }

    actual fun findCpuTemperatureLocation(): String? {
        return null
    }

    actual fun getCpuTemp(path: String): Float? {
        return null
    }

    companion object {
        private const val REFRESH_DELAY = 5000L
        private const val ID_THERMAL_STATE = 1000
    }
}