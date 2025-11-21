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
package com.kgurgul.cpuinfo.features.processes

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.FakeUserPreferencesRepository
import com.kgurgul.cpuinfo.data.provider.FakeProcessesProvider
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.domain.result.FilterProcessesInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class ProcessesViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val processes = TestData.processes
    private val fakeProcessesProvider =
        FakeProcessesProvider(processesList = processes, processesSupported = true)
    private val processesDataObservable =
        ProcessesDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            processesProvider = fakeProcessesProvider,
        )
    private val fakeUserPreferencesRepository =
        FakeUserPreferencesRepository(preferencesFlow = flowOf(TestData.userPreferences))
    private val savedStateHandle = SavedStateHandle()
    private val filterProcessesInteractor =
        FilterProcessesInteractor(dispatchersProvider = coroutineTestRule.testDispatcherProvider)

    private lateinit var viewModel: ProcessesViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel =
            ProcessesViewModel(
                processesDataObservable = processesDataObservable,
                savedStateHandle = savedStateHandle,
                userPreferencesRepository = fakeUserPreferencesRepository,
                filterProcessesInteractor = filterProcessesInteractor,
            )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState =
            ProcessesViewModel.UiState(isLoading = false, processes = processes.toImmutableList())

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }

    @Test
    fun onSearchQueryChanged() = runTest {
        val expectedUiStates =
            listOf(
                ProcessesViewModel.UiState(
                    isLoading = false,
                    processes = processes.toImmutableList(),
                ),
                ProcessesViewModel.UiState(
                    isLoading = false,
                    processes = persistentListOf(processes[1]),
                ),
            )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiStates[0], awaitItem())
            viewModel.onSearchQueryChanged("nazwa")
            assertEquals(expectedUiStates[1], awaitItem())
        }
    }
}
