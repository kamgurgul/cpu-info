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
package com.kgurgul.cpuinfo.features

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.FakeUserPreferencesRepository
import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import com.kgurgul.cpuinfo.data.provider.FakeApplicationsDataProvider
import com.kgurgul.cpuinfo.data.provider.FakeProcessesProvider
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest

class HostViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeProcessesProvider = FakeProcessesProvider(processesSupported = true)
    private val processesDataObservable =
        ProcessesDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            processesProvider = fakeProcessesProvider,
        )
    private val fakeApplicationsProvider =
        FakeApplicationsDataProvider(applicationsSupported = true)
    private val applicationsDataObservable =
        ApplicationsDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            applicationsDataProvider = fakeApplicationsProvider,
        )
    private val userPreferenceSharedFlow = MutableSharedFlow<UserPreferences>(replay = 1)
    private val userPreferencesRepository =
        FakeUserPreferencesRepository(preferencesFlow = userPreferenceSharedFlow)

    private lateinit var viewModel: HostViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel =
            HostViewModel(
                processesDataObservable = processesDataObservable,
                applicationsDataObservable = applicationsDataObservable,
                userPreferencesRepository = userPreferencesRepository,
            )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        userPreferenceSharedFlow.emit(TestData.userPreferences)
        val expectedUiState =
            HostViewModel.UiState(
                isLoading = false,
                isProcessSectionVisible = true,
                isApplicationSectionVisible = true,
                darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            )

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }
}
