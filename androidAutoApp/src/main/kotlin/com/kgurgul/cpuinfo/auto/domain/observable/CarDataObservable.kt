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
package com.kgurgul.cpuinfo.auto.domain.observable

import com.kgurgul.cpuinfo.auto.data.provider.ICarDataProvider
import com.kgurgul.cpuinfo.auto.domain.model.CarData
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan

/**
 * Combines all car hardware streams into single [CarData]. Invoking it again restarts all
 * listeners, e.g. after the user granted car permissions.
 */
class CarDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val carDataProvider: ICarDataProvider,
) : MutableInteractor<Unit, CarData>() {

    // Car App Library expects hardware listeners to be registered from the main thread
    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.main

    override fun createObservable(params: Unit): Flow<CarData> {
        val initialData = CarData(missingPermissions = carDataProvider.getMissingPermissions())
        return merge(
                carDataProvider.observeHost().reducer { copy(host = it) },
                carDataProvider.observeModel().reducer { copy(model = it) },
                carDataProvider.observeEnergyProfile().reducer { copy(energyProfile = it) },
                carDataProvider.observeEnergyLevel().reducer { copy(energyLevel = it) },
                carDataProvider.observeSpeed().reducer { copy(speed = it) },
                carDataProvider.observeMileage().reducer { copy(mileage = it) },
                carDataProvider.observeTollCard().reducer { copy(tollCard = it) },
                carDataProvider.observeCompass().reducer { copy(compass = it) },
                carDataProvider.observeAccelerometer().reducer { copy(accelerometer = it) },
                carDataProvider.observeGyroscope().reducer { copy(gyroscope = it) },
            )
            .scan(initialData) { carData, reducer -> carData.reducer() }
    }

    private fun <T> Flow<T>.reducer(block: CarData.(T) -> CarData): Flow<CarData.() -> CarData> =
        map { value ->
            { block(value) }
        }
}
