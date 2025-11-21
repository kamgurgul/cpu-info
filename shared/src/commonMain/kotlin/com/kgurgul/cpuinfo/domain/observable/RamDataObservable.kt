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

import com.kgurgul.cpuinfo.data.provider.IRamDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class RamDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val ramDataProvider: IRamDataProvider,
) : ImmutableInteractor<Unit, RamData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val total = ramDataProvider.getTotalBytes()
            val available = ramDataProvider.getAvailableBytes()
            val availablePercentage =
                if (total != 0L) (available.toDouble() / total.toDouble() * 100.0).toInt() else 0

            val threshold = ramDataProvider.getThreshold()
            emit(
                RamData(
                    total = total,
                    available = available,
                    availablePercentage = availablePercentage,
                    threshold = threshold,
                    additionalData = ramDataProvider.getAdditionalData(),
                )
            )
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 5000L
    }
}
