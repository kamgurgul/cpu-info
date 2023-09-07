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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import org.junit.Rule
import org.mockito.kotlin.mock

class TemperatureViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val mockTemperatureDataObservable = mock<TemperatureDataObservable>()

    private val observedUiStates = mutableListOf<TemperatureViewModel.UiState>()
    private lateinit var viewModel: TemperatureViewModel

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = TemperatureViewModel(
            temperatureDataObservable = mockTemperatureDataObservable,
        )
    }
}