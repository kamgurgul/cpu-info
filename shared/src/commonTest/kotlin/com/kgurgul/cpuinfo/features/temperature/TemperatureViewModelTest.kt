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
package com.kgurgul.cpuinfo.features.temperature

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.FakeUserPreferencesRepository
import com.kgurgul.cpuinfo.data.provider.FakeTemperatureProvider
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest

class TemperatureViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val temperatureData = TestData.temperatureData
    private val fakeUserPreferencesRepository = FakeUserPreferencesRepository()
    private val fakeTemperatureProvider =
        FakeTemperatureProvider(
            sensorsFlow = emptyFlow(),
            cpuTempLocation = "/sys/class/thermal/thermal_zone0/temp",
            cpuTemp = 10f,
        )
    private val temperatureFormatter = TemperatureFormatter(fakeUserPreferencesRepository)
    private val temperatureDataObservable =
        TemperatureDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            temperatureProvider = fakeTemperatureProvider,
        )

    private lateinit var viewModel: TemperatureViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel =
            TemperatureViewModel(
                temperatureFormatter = temperatureFormatter,
                temperatureDataObservable = temperatureDataObservable,
            )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState =
            TemperatureViewModel.UiState(
                temperatureFormatter = temperatureFormatter,
                isLoading = false,
                temperatureItems = temperatureData.toImmutableList(),
            )

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }
}
