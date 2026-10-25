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
package com.kgurgul.cpuinfo.auto.data.provider

import com.kgurgul.cpuinfo.auto.domain.model.CarEnergyLevelData
import com.kgurgul.cpuinfo.auto.domain.model.CarEnergyProfileData
import com.kgurgul.cpuinfo.auto.domain.model.CarHostData
import com.kgurgul.cpuinfo.auto.domain.model.CarMileageData
import com.kgurgul.cpuinfo.auto.domain.model.CarModelData
import com.kgurgul.cpuinfo.auto.domain.model.CarProperty
import com.kgurgul.cpuinfo.auto.domain.model.CarSpeedData
import com.kgurgul.cpuinfo.auto.domain.model.TollCardState
import kotlinx.coroutines.flow.Flow

interface ICarDataProvider {

    fun getMissingPermissions(): List<String>

    fun observeHost(): Flow<CarHostData?>

    fun observeModel(): Flow<CarModelData>

    fun observeEnergyProfile(): Flow<CarEnergyProfileData>

    fun observeEnergyLevel(): Flow<CarEnergyLevelData>

    fun observeSpeed(): Flow<CarSpeedData>

    fun observeMileage(): Flow<CarMileageData>

    fun observeTollCard(): Flow<CarProperty<TollCardState>>

    fun observeCompass(): Flow<CarProperty<List<Float>>>

    fun observeAccelerometer(): Flow<CarProperty<List<Float>>>

    fun observeGyroscope(): Flow<CarProperty<List<Float>>>
}
