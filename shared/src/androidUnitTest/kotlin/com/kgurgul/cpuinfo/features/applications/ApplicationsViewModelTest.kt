package com.kgurgul.cpuinfo.features.applications

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.domain.action.ExternalAppAction
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_open
import com.kgurgul.cpuinfo.shared.cpu_open
import com.kgurgul.cpuinfo.shared.cpu_uninstall
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
    private val mockExternalAppAction = mock<ExternalAppAction>()

    private val observedUiStates = mutableListOf<ApplicationsViewModel.UiState>()
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
                nativeLibs = listOf("test").toImmutableList(),
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("src/test/resources").toImmutableList(),
            ),
        )
        createViewModel()

        viewModel.onNativeLibsClicked(listOf("test"))
        viewModel.onNativeLibsClicked(listOf("src/test/resources"))

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
                nativeLibs = listOf("test").toImmutableList(),
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                isDialogVisible = true,
                nativeLibs = listOf("src/test/resources").toImmutableList(),
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
        )
        createViewModel()
        viewModel.onNativeLibsClicked(listOf("test"))
        viewModel.onNativeLibsClicked(listOf("src/test/resources"))

        viewModel.onNativeLibsDialogDismissed()

        assertEquals(expectedUiStates, observedUiStates)
    }

    @Test
    fun `On native libs name clicked`() {
        createViewModel()

        viewModel.onNativeLibsNameClicked("test")

        verify(mockExternalAppAction).searchOnWeb(eq("test"))
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
                snackbarMessage = Res.string.cpu_uninstall,
            ),
        )
        createViewModel()

        viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")
        viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo.debug")

        assertEquals(expectedUiStates, observedUiStates)
        verify(mockExternalAppAction).uninstall(eq("com.kgurgul.cpuinfo.debug"))
    }

    @Test
    fun `On app settings clicked`() {
        createViewModel()

        viewModel.onAppSettingsClicked("com.kgurgul.cpuinfo.debug")

        verify(mockExternalAppAction).openSettings(eq("com.kgurgul.cpuinfo.debug"))
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
                snackbarMessage = Res.string.cpu_uninstall,
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
        whenever(mockExternalAppAction.launch("com.kgurgul.cpuinfo"))
            .doReturn(kotlin.Result.failure(Exception()))
        val expectedUiStates = listOf(
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            ),
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                snackbarMessage = Res.string.app_open,
            ),
        )
        createViewModel()

        viewModel.onApplicationClicked("com.kgurgul.cpuinfo")

        assertEquals(expectedUiStates, observedUiStates)
        verify(mockExternalAppAction).launch(eq("com.kgurgul.cpuinfo"))
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
                snackbarMessage = Res.string.cpu_open,
            ),
        )
        createViewModel()

        viewModel.onApplicationClicked("com.kgurgul.cpuinfo")
        viewModel.onApplicationClicked("com.kgurgul.cpuinfo.debug")

        assertEquals(expectedUiStates, observedUiStates)
        verify(mockExternalAppAction).launch(eq("com.kgurgul.cpuinfo.debug"))
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
        viewModel = ApplicationsViewModel(
            applicationsDataObservable = mockApplicationsDataObservable,
            getPackageNameInteractor = mockGetPackageNameInteractor,
            userPreferencesRepository = mockUserPreferencesRepository,
            externalAppAction = mockExternalAppAction,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}