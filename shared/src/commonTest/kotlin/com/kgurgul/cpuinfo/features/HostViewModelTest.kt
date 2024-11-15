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

    private val fakeProcessesProvider = FakeProcessesProvider(
        processesSupported = true,
    )
    private val processesDataObservable = ProcessesDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        processesProvider = fakeProcessesProvider,
    )
    private val fakeApplicationsProvider = FakeApplicationsDataProvider(
        applicationsSupported = true,
    )
    private val applicationsDataObservable = ApplicationsDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        applicationsDataProvider = fakeApplicationsProvider,
    )
    private val userPreferenceSharedFlow = MutableSharedFlow<UserPreferences>(replay = 1)
    private val userPreferencesRepository = FakeUserPreferencesRepository(
        preferencesFlow = userPreferenceSharedFlow,
    )

    private lateinit var viewModel: HostViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = HostViewModel(
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
        val expectedUiState = HostViewModel.UiState(
            isLoading = false,
            isProcessSectionVisible = true,
            isApplicationSectionVisible = true,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
