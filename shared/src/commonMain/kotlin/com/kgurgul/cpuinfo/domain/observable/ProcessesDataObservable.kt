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

import com.kgurgul.cpuinfo.data.provider.IProcessesProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class ProcessesDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val processesProvider: IProcessesProvider,
) : ImmutableInteractor<ProcessesDataObservable.Params, List<ProcessItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        while (true) {
            val processes = processesProvider.getProcessList()
            emit(
                when (params.sortOrder) {
                    SortOrder.ASCENDING -> processes.sorted()
                    SortOrder.DESCENDING -> processes.sortedDescending()
                    else -> processes
                }
            )
            delay(REFRESH_DELAY)
        }
    }

    fun areProcessesSupported() = processesProvider.areProcessesSupported()

    data class Params(val sortOrder: SortOrder = SortOrder.NONE)

    companion object {
        private const val REFRESH_DELAY = 3000L
    }
}
