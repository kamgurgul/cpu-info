package com.kgurgul.cpuinfo.features.applications

import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationsViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockApplicationsDataObservable = mock<ApplicationsDataObservable> {
        on { observe() } doReturn flowOf()
    }
    private val mockGetPackageNameInteractor = mock<GetPackageNameInteractor>()
    private val testUserPreferences = TestData.userPreferences
    private val mockUserPreferencesRepository = mock<UserPreferencesRepository> {
        on { userPreferencesFlow } doReturn flowOf(testUserPreferences)
    }

    private val observedUiStates = mutableListOf<ApplicationsViewModel.UiState>()
    private val observedEvents = mutableListOf<ApplicationsViewModel.Event>()
    private lateinit var viewModel: ApplicationsViewModel

    @Test
    fun `Load initial data with passed user preferences`() {
        // Given
        val expectedUiState = listOf(
            ApplicationsViewModel.UiState(
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

    @Test
    fun `Handle applications result`() = runTest {
        whenever(mockApplicationsDataObservable.observe()).doReturn(
            flowOf(
                Result.Loading,
                Result.Success(TestData.extendedApplicationsData),
            )
        )
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                applications = TestData.extendedApplicationsData.toImmutableList(),
            ),
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    @Test
    fun `On sort order change clicked`() = runTest {
        createViewModel()

        viewModel.onSortOrderChange(false)

        verify(mockUserPreferencesRepository).setApplicationsSortingOrder(eq(false))
    }

    @Test
    fun `On system apps switched`() = runTest {
        createViewModel()

        viewModel.onSystemAppsSwitched(false)

        verify(mockUserPreferencesRepository).setApplicationsWithSystemApps(eq(false))
    }

    @Test
    fun `On native libs clicked`() {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("mockito-extensions").toImmutableList(),
            ),
        )
        createViewModel()

        viewModel.onNativeLibsClicked("test")
        viewModel.onNativeLibsClicked("src/test/resources")

        assertEquals(expectedUiStates, observedUiStates)
    }

    @Test
    fun `On native libs dialog dismissed`() {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("mockito-extensions").toImmutableList(),
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
        )
        createViewModel()
        viewModel.onNativeLibsClicked("test")
        viewModel.onNativeLibsClicked("src/test/resources")

        viewModel.onNativeLibsDialogDismissed()

        assertEquals(expectedUiStates, observedUiStates)
    }

    @Test
    fun `On native libs name clicked`() {
        val expectedEvents = listOf<ApplicationsViewModel.Event>(
            ApplicationsViewModel.Event.SearchNativeLib("test"),
        )
        createViewModel()

        viewModel.onNativeLibsNameClicked("test")

        assertEquals(expectedEvents, observedEvents)
    }

    @Test
    fun `On app uninstall clicked`() = runTest {
        whenever(mockGetPackageNameInteractor.invoke(Unit)).doReturn("com.kgurgul.cpuinfo")
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = R.string.cpu_uninstall,
            ),
        )
        val expectedEvents = listOf<ApplicationsViewModel.Event>(
            ApplicationsViewModel.Event.UninstallApp("com.kgurgul.cpuinfo.debug"),
        )
        createViewModel()

        viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")
        viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo.debug")

        assertEquals(expectedUiStates, observedUiStates)
        assertEquals(expectedEvents, observedEvents)
    }

    @Test
    fun `On app settings clicked`() {
        val expectedEvents = listOf<ApplicationsViewModel.Event>(
            ApplicationsViewModel.Event.OpenAppSettings("com.kgurgul.cpuinfo.debug"),
        )
        createViewModel()

        viewModel.onAppSettingsClicked("com.kgurgul.cpuinfo.debug")

        assertEquals(expectedEvents, observedEvents)
    }

    @Test
    fun `On snackbar dismissed`() = runTest {
        whenever(mockGetPackageNameInteractor.invoke(Unit)).doReturn("com.kgurgul.cpuinfo")
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = R.string.cpu_uninstall,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
        )
        createViewModel()
        viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")

        viewModel.onSnackbarDismissed()

        assertEquals(expectedUiStates, observedUiStates)
    }

    @Test
    fun `On cannot open app`() {
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = R.string.app_open,
            ),
        )
        createViewModel()

        viewModel.onCannotOpenApp()

        assertEquals(expectedUiStates, observedUiStates)
    }

    @Test
    fun `On application clicked`() = runTest {
        whenever(mockGetPackageNameInteractor.invoke(Unit)).doReturn("com.kgurgul.cpuinfo")
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = R.string.cpu_open,
            ),
        )
        val expectedEvents = listOf<ApplicationsViewModel.Event>(
            ApplicationsViewModel.Event.OpenApp("com.kgurgul.cpuinfo.debug"),
        )
        createViewModel()

        viewModel.onApplicationClicked("com.kgurgul.cpuinfo")
        viewModel.onApplicationClicked("com.kgurgul.cpuinfo.debug")

        assertEquals(expectedUiStates, observedUiStates)
        assertEquals(expectedEvents, observedEvents)
    }

    @Test
    fun `On refresh applications clicked`() {
        createViewModel()

        viewModel.onRefreshApplications()

        verify(mockApplicationsDataObservable, times(2)).invoke(
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
        viewModel = ApplicationsViewModel(
            applicationsDataObservable = mockApplicationsDataObservable,
            getPackageNameInteractor = mockGetPackageNameInteractor,
            userPreferencesRepository = mockUserPreferencesRepository,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
            it.events
                .onEach(observedEvents::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}