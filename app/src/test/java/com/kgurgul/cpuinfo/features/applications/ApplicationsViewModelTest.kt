package com.kgurgul.cpuinfo.features.applications

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class ApplicationsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val mockApplicationsDataObservable = mock<ApplicationsDataObservable> {
        on { observe() } doReturn flowOf()
    }
    private val mockGetPackageNameInteractor = mock<GetPackageNameInteractor>()
    private val testUserPreferences = TestData.userPreferences
    private val mockUserPreferencesRepository = mock<UserPreferencesRepository> {
        on { userPreferencesFlow } doReturn flowOf(testUserPreferences)
    }

    private val observedUiStates = mutableListOf<NewApplicationsViewModel.UiState>()
    private val observedEvents = mutableListOf<NewApplicationsViewModel.Event>()
    private lateinit var viewModel: NewApplicationsViewModel

    @Test
    fun `Load initial data with passed user preferences`() {
        // Given
        val expectedUiState = listOf(
            NewApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            )
        )

        // When
        createViewModel()

        // Then
        assertEquals(expectedUiState, observedUiStates)
        verify(mockApplicationsDataObservable).invoke(
            eq(
                ApplicationsDataObservable.Params(
                    withSystemApps = testUserPreferences.withSystemApps,
                    sortOrder = SortOrder.ASCENDING,
                )
            )
        )
    }

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