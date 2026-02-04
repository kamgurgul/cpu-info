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

import com.kgurgul.cpuinfo.data.provider.ITemperatureProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TemperatureDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val temperatureProvider: ITemperatureProvider,
) : ImmutableInteractor<Unit, TemperatureResult>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    fun isAdminRequiredFlow(): Flow<Boolean> = flow { emit(temperatureProvider.isAdminRequired()) }

    private val cachedTemperatures = mutableListOf<TemperatureItem>()

    override fun createObservable(params: Unit) = channelFlow {
        send(TemperatureResult(isLoading = true, items = emptyList()))

        var hasReceivedData = false

        val timeoutJob = launch {
            delay(LOADING_TIMEOUT)
            if (!hasReceivedData) {
                send(TemperatureResult(isLoading = false, items = emptyList()))
            }
        }

        temperatureProvider.sensorsFlow.collect { temperatureItem ->
            hasReceivedData = true
            timeoutJob.cancel()
            cachedTemperatures.apply {
                removeAll { it.id == temperatureItem.id }
                add(temperatureItem)
                sortBy { it.id }
            }
            send(TemperatureResult(isLoading = false, items = cachedTemperatures.toList()))
        }
    }

    companion object {
        private const val LOADING_TIMEOUT = 5000L
    }
}

data class TemperatureResult(val isLoading: Boolean, val items: List<TemperatureItem>)
