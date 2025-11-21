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
package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.IScreenDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.orientation
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetScreenDataInteractor(
    private val dispatchersProvider: IDispatchersProvider,
    private val screenDataProvider: IScreenDataProvider,
) : ImmutableInteractor<Unit, List<ItemValue>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    private val initialDataFlow = flow { emit(screenDataProvider.getData()) }

    override fun createObservable(params: Unit): Flow<List<ItemValue>> {
        val orientationFlow =
            screenDataProvider
                .getOrientationFlow()
                .onStart { emit(INITIAL_ORIENTATION) }
                .map { orientation -> ItemValue.NameResource(Res.string.orientation, orientation) }
        return orientationFlow.flatMapLatest { orientation ->
            initialDataFlow.map { it + orientation }
        }
    }

    companion object {
        private const val INITIAL_ORIENTATION = "Unknown"
    }
}
