package com.kgurgul.cpuinfo.features

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.data.local.UserPreferences
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class HostViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockProcessesDataObservable = mock<ProcessesDataObservable> {
        on { areProcessesSupported() } doReturn true
    }
    private val mockApplicationsDataObservable = mock<ApplicationsDataObservable> {
        on { areApplicationsSupported() } doReturn true
    }
    private val userPreferenceSharedFlow = MutableSharedFlow<UserPreferences>(replay = 1)
    private val mockUserPreferencesRepository = mock<IUserPreferencesRepository> {
        on { userPreferencesFlow } doReturn userPreferenceSharedFlow
    }

    private val observedUiStates = mutableListOf<HostViewModel.UiState>()
    private lateinit var viewModel: HostViewModel

    @Test
    fun `Create VM`() = runTest {
        userPreferenceSharedFlow.emit(TestData.userPreferences)
        val expectedUiStates = listOf(
            HostViewModel.UiState(
                isLoading = false,
                isProcessSectionVisible = true,
                isApplicationSectionVisible = true,
                darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            )
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = HostViewModel(
            processesDataObservable = mockProcessesDataObservable,
            applicationsDataObservable = mockApplicationsDataObservable,
            userPreferencesRepository = mockUserPreferencesRepository,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}