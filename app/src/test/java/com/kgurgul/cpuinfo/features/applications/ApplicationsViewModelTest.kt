package com.kgurgul.cpuinfo.features.applications

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.mockito.kotlin.mock

class ApplicationsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val mockApplicationsDataObservable = mock<ApplicationsDataObservable>()
    private val mockGetPackageNameInteractor = mock<GetPackageNameInteractor>()
    private val mockUserPreferencesRepository = mock<UserPreferencesRepository>()

    private val observedUiStates = mutableListOf<NewApplicationsViewModel.UiState>()
    private val observedEvents = mutableListOf<NewApplicationsViewModel.Event>()
    private lateinit var viewModel: NewApplicationsViewModel

    private fun createViewModel() {
        observedUiStates.clear()
        observedEvents.clear()
        viewModel = NewApplicationsViewModel(
            applicationsDataObservable = mockApplicationsDataObservable,
            getPackageNameInteractor = mockGetPackageNameInteractor,
            userPreferencesRepository = mockUserPreferencesRepository,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
            it.events.observeForever(observedEvents::add)
        }
    }
}