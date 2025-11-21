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
package com.kgurgul.cpuinfo.features.information.cpu

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeCpuDataNativeProvider
import com.kgurgul.cpuinfo.data.provider.FakeCpuDataProvider
import com.kgurgul.cpuinfo.domain.observable.CpuDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class CpuInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val cpuData = TestData.cpuData
    private val fakeCpuDataProvider = FakeCpuDataProvider()
    private val fakeCpuDataNativeProvider = FakeCpuDataNativeProvider()
    private val cpuDataObservable =
        CpuDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            cpuDataProvider = fakeCpuDataProvider,
            cpuDataNativeProvider = fakeCpuDataNativeProvider,
        )

    private lateinit var viewModel: CpuInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = CpuInfoViewModel(cpuDataObservable = cpuDataObservable)
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = CpuInfoViewModel.UiState(isInitializing = false, cpuData = cpuData)

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }
}
