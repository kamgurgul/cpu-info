/*
 * Copyright 2017 KG Soft
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

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class TemperatureViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val temperatureData = TestData.temperatureData
    private val mockTemperatureFormatter = mock<TemperatureFormatter>()
    private val mockTemperatureDataObservable = mock<TemperatureDataObservable> {
        on { observe(anyOrNull()) } doReturn flowOf(temperatureData)
    }

    private val observedUiStates = mutableListOf<TemperatureViewModel.UiState>()
    private lateinit var viewModel: TemperatureViewModel

    @Test
    fun `Get temperature data observable`() {
        val expectedUiStates = listOf(
            TemperatureViewModel.UiState(
                temperatureFormatter = mockTemperatureFormatter,
                isLoading = false,
                temperatureItems = temperatureData.toPersistentList(),
            )
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = TemperatureViewModel(
            temperatureFormatter = mockTemperatureFormatter,
            temperatureDataObservable = mockTemperatureDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}