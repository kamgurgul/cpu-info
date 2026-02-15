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

import com.kgurgul.cpuinfo.data.provider.ICpuDataNativeProvider
import com.kgurgul.cpuinfo.data.provider.ICpuDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu_abi
import com.kgurgul.cpuinfo.shared.cpu_cores
import com.kgurgul.cpuinfo.shared.cpu_l1d
import com.kgurgul.cpuinfo.shared.cpu_l1i
import com.kgurgul.cpuinfo.shared.cpu_l2
import com.kgurgul.cpuinfo.shared.cpu_l3
import com.kgurgul.cpuinfo.shared.cpu_l4
import com.kgurgul.cpuinfo.shared.cpu_soc_name
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class CpuDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val cpuDataProvider: ICpuDataProvider,
    private val cpuDataNativeProvider: ICpuDataNativeProvider,
) : ImmutableInteractor<Unit, CpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val processorName = cpuDataNativeProvider.getCpuName()
            val abi = cpuDataProvider.getAbi()
            val logicalCoresCount = cpuDataProvider.getNumberOfLogicalCores()
            val physicalCoresCount = cpuDataProvider.getNumberOfPhysicalCores()
            val frequencies = mutableListOf<CpuData.Frequency>()
            val l1dCaches =
                cpuDataNativeProvider.getL1dCaches()?.joinToString(separator = "\n") {
                    Utils.humanReadableByteCount(it.toLong())
                } ?: ""
            val l1iCaches =
                cpuDataNativeProvider.getL1iCaches()?.joinToString(separator = "\n") {
                    Utils.humanReadableByteCount(it.toLong())
                } ?: ""
            val l2Caches =
                cpuDataNativeProvider.getL2Caches()?.joinToString(separator = "\n") {
                    Utils.humanReadableByteCount(it.toLong())
                } ?: ""
            val l3Caches =
                cpuDataNativeProvider.getL3Caches()?.joinToString(separator = "\n") {
                    Utils.humanReadableByteCount(it.toLong())
                } ?: ""
            val l4Caches =
                cpuDataNativeProvider.getL4Caches()?.joinToString(separator = "\n") {
                    Utils.humanReadableByteCount(it.toLong())
                } ?: ""
            for (i in 0 until logicalCoresCount) {
                val (min, max) = cpuDataProvider.getMinMaxFreq(i)
                val current = cpuDataProvider.getCurrentFreq(i)
                if (min != -1L && max != -1L) {
                    frequencies.add(CpuData.Frequency(min, max, current))
                }
            }
            val cpuItems = buildList {
                add(ItemValue.NameResource(Res.string.cpu_soc_name, processorName))
                add(ItemValue.NameResource(Res.string.cpu_abi, abi))
                add(ItemValue.NameResource(Res.string.cpu_cores, physicalCoresCount.toString()))
                addAll(cpuDataNativeProvider.getExtraItems())
                if (l1dCaches.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.cpu_l1d, l1dCaches))
                }
                if (l1iCaches.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.cpu_l1i, l1iCaches))
                }
                if (l2Caches.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.cpu_l2, l2Caches))
                }
                if (l3Caches.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.cpu_l3, l3Caches))
                }
                if (l4Caches.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.cpu_l4, l4Caches))
                }
            }
            emit(
                CpuData(
                    cpuItems = cpuItems,
                    frequencies = frequencies,
                )
            )
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 1000L
    }
}
