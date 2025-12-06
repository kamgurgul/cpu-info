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
package com.kgurgul.cpuinfo.features.information.ram

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeRamDataProvider
import com.kgurgul.cpuinfo.domain.observable.RamDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class RamInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val ramData = TestData.ramData
    private val fakeRamDataProvider = FakeRamDataProvider()
    private val ramDataObservable =
        RamDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            ramDataProvider = fakeRamDataProvider,
        )

    private lateinit var viewModel: RamInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = RamInfoViewModel(ramDataObservable = ramDataObservable)
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = RamInfoViewModel.UiState(isInitializing = false, ramData = ramData)

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }
}
