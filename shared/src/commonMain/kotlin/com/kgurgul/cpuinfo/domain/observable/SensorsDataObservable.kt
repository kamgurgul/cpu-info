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
package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.SensorsInfoProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SensorsDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val sensorsInfoProvider: SensorsInfoProvider,
) : ImmutableInteractor<Unit, List<SensorData>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.default

    override fun createObservable(params: Unit): Flow<List<SensorData>> {
        return sensorsInfoProvider.getSensorData()
    }
}
