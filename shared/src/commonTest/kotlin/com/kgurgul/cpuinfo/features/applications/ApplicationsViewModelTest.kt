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
package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.FakeUserPreferencesRepository
import com.kgurgul.cpuinfo.data.provider.FakeApplicationsDataProvider
import com.kgurgul.cpuinfo.data.provider.FakePackageNameProvider
import com.kgurgul.cpuinfo.domain.action.FakeExternalAppAction
import com.kgurgul.cpuinfo.domain.model.UninstallOutcome
import com.kgurgul.cpuinfo.domain.model.UninstallTarget
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.FilterApplicationsInteractor
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_open
import com.kgurgul.cpuinfo.shared.app_uninstall_error
import com.kgurgul.cpuinfo.shared.app_uninstall_restart_required
import com.kgurgul.cpuinfo.shared.app_uninstall_unconfirmed
import com.kgurgul.cpuinfo.shared.cpu_open
import com.kgurgul.cpuinfo.shared.cpu_uninstall
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class ApplicationsViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeApplicationsDataProvider = FakeApplicationsDataProvider()
    private val applicationsDataObservable =
        ApplicationsDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            applicationsDataProvider = fakeApplicationsDataProvider,
        )
    private val fakePackageNameProvider = FakePackageNameProvider()
    private val getPackageNameInteractor =
        GetPackageNameInteractor(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            packageNameProvider = fakePackageNameProvider,
        )
    private val testUserPreferences = TestData.userPreferences
    private val fakeUserPreferencesFlow = flowOf(testUserPreferences)
    private val fakeUserPreferencesRepository =
        FakeUserPreferencesRepository(preferencesFlow = fakeUserPreferencesFlow)
    private val fakeExternalAppAction = FakeExternalAppAction()
    private val savedStateHandle = SavedStateHandle()
    private val filterApplicationsInteractor =
        FilterApplicationsInteractor(dispatchersProvider = coroutineTestRule.testDispatcherProvider)

    private lateinit var viewModel: ApplicationsViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        fakeUserPreferencesRepository.reset()
        fakeExternalAppAction.reset()
        viewModel =
            ApplicationsViewModel(
                savedStateHandle = savedStateHandle,
                applicationsDataObservable = applicationsDataObservable,
                getPackageNameInteractor = getPackageNameInteractor,
                userPreferencesRepository = fakeUserPreferencesRepository,
                externalAppAction = fakeExternalAppAction,
                filterApplicationsInteractor = filterApplicationsInteractor,
                applicationsDataProvider = fakeApplicationsDataProvider,
            )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun loadInitialData() = runTest {
        val expectedUiState =
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
            )

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }

    @Test
    fun handleApplicationsResult() = runTest {
        fakeApplicationsDataProvider.installedApplications = TestData.extendedApplicationsData
        val expectedUiState =
            ApplicationsViewModel.UiState(
                withSystemApps = testUserPreferences.withSystemApps,
                isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                applications = TestData.extendedApplicationsData.toImmutableList(),
            )

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }

    @Test
    fun onSortOrderChangeClicked() {
        viewModel.onSortOrderChange(false)

        assertTrue(fakeUserPreferencesRepository.isSetApplicationsSortingOrderCalled)
    }

    @Test
    fun onSystemAppsSwitched() {
        viewModel.onSystemAppsSwitched(false)

        assertTrue(fakeUserPreferencesRepository.isSetApplicationsWithSystemAppsCalled)
    }

    @Test
    fun onNativeLibsClicked() = runTest {
        val expectedUiStates =
            listOf(
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

        viewModel.uiStateFlow.test {
            viewModel.onNativeLibsClicked(listOf("test"))
            viewModel.onNativeLibsClicked(listOf("src/test/resources"))

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onNativeLibsDialogDismissed() = runTest {
        val expectedUiStates =
            listOf(
                ApplicationsViewModel.UiState(
                    withSystemApps = testUserPreferences.withSystemApps,
                    isSortAscending = testUserPreferences.isApplicationsSortingAscending,
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

        viewModel.uiStateFlow.test {
            viewModel.onNativeLibsClicked(listOf("src/test/resources"))
            viewModel.onNativeLibsDialogDismissed()

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onNativeLibsNameClicked() {
        viewModel.onNativeLibsNameClicked("test")

        assertTrue(fakeExternalAppAction.isSearchOnWebCalled)
    }

    @Test
    fun onAppUninstallClicked() = runTest {
        val expectedUiStates =
            listOf(
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

        viewModel.uiStateFlow.test {
            viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")
            viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo.debug")

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onAppSettingsClicked() {
        viewModel.onAppSettingsClicked("com.kgurgul.cpuinfo.debug")

        assertTrue(fakeExternalAppAction.isOpenSettingsCalled)
    }

    @Test
    fun onSnackbarDismissed() = runTest {
        val expectedUiStates =
            listOf(
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

        viewModel.uiStateFlow.test {
            viewModel.onAppUninstallClicked("com.kgurgul.cpuinfo")
            viewModel.onSnackbarDismissed()

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onCannotOpenApp() = runTest {
        fakeExternalAppAction.isLaunchSuccess = false
        val expectedUiStates =
            listOf(
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

        viewModel.uiStateFlow.test {
            viewModel.onApplicationClicked("com.kgurgul.cpuinfo.debug")

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onCurrentApplicationClicked() = runTest {
        val expectedUiStates =
            listOf(
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

        viewModel.uiStateFlow.test {
            viewModel.onApplicationClicked("com.kgurgul.cpuinfo")

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onRefreshApplicationsClicked() = runTest {
        val expectedUiStates =
            listOf(
                ApplicationsViewModel.UiState(
                    withSystemApps = testUserPreferences.withSystemApps,
                    isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                ),
                ApplicationsViewModel.UiState(
                    isLoading = true,
                    withSystemApps = testUserPreferences.withSystemApps,
                    isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                ),
                ApplicationsViewModel.UiState(
                    withSystemApps = testUserPreferences.withSystemApps,
                    isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                ),
            )

        viewModel.uiStateFlow.test {
            viewModel.onRefreshApplications()

            expectedUiStates.forEach { expectedUiState ->
                assertEquals(expectedUiState, awaitItem())
            }
        }
    }

    @Test
    fun onSearchQueryChanged() = runTest {
        fakeApplicationsDataProvider.installedApplications = TestData.extendedApplicationsData
        val expectedUiStates =
            listOf(
                ApplicationsViewModel.UiState(
                    withSystemApps = testUserPreferences.withSystemApps,
                    isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                    applications = TestData.extendedApplicationsData.toImmutableList(),
                ),
                ApplicationsViewModel.UiState(
                    withSystemApps = testUserPreferences.withSystemApps,
                    isSortAscending = testUserPreferences.isApplicationsSortingAscending,
                    applications = persistentListOf(TestData.extendedApplicationsData[2]),
                ),
            )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiStates[0], awaitItem())
            viewModel.onSearchQueryChanged("app3")
            assertEquals(expectedUiStates[1], awaitItem())
        }
    }

    @Test
    fun desktopRemovalTracksProgressAndPreventsDuplicateLaunches() = runTest {
        val target = UninstallTarget.MacBundle("/Applications/Example.app")
        val completion = CompletableDeferred<Result<UninstallOutcome>>()
        fakeExternalAppAction.desktopUninstall = { completion.await() }
        viewModel.uiStateFlow.test {
            awaitItem()
            viewModel.onDesktopAppUninstallClicked(target)
            viewModel.onDesktopAppUninstallClicked(target)
            runCurrent()
            assertEquals(1, fakeExternalAppAction.desktopUninstallCalls)
            assertTrue(target in expectMostRecentItem().uninstallingTargets)
            completion.complete(Result.success(UninstallOutcome.REMOVED))
            runCurrent()
            assertFalse(target in expectMostRecentItem().uninstallingTargets)
        }
    }

    @Test
    fun desktopRemovalReportsRestartAndUnconfirmedOutcomes() = runTest {
        val target = UninstallTarget.MacBundle("/Applications/Example.app")
        viewModel.uiStateFlow.test {
            awaitItem()
            for ((outcome, message) in
            listOf(
                UninstallOutcome.RESTART_REQUIRED to Res.string.app_uninstall_restart_required,
                UninstallOutcome.UNCONFIRMED to Res.string.app_uninstall_unconfirmed,
                UninstallOutcome.CANCELLED to null,
            )) {
                fakeExternalAppAction.desktopUninstall = { Result.success(outcome) }
                viewModel.onDesktopAppUninstallClicked(target)
                runCurrent()
                val state = expectMostRecentItem()
                assertEquals(message, state.snackbarMessage)
                assertTrue(state.uninstallingTargets.isEmpty())
            }
        }
    }

    @Test
    fun desktopRemovalFailureClearsProgressAndAllowsRetry() = runTest {
        val target = UninstallTarget.MacBundle("/Applications/Example.app")
        fakeExternalAppAction.desktopUninstall = {
            Result.failure(IllegalStateException("Permission denied"))
        }
        viewModel.uiStateFlow.test {
            awaitItem()
            viewModel.onDesktopAppUninstallClicked(target)
            runCurrent()
            val failed = expectMostRecentItem()
            assertEquals(Res.string.app_uninstall_error, failed.snackbarMessage)
            assertTrue(failed.uninstallingTargets.isEmpty())
            fakeExternalAppAction.desktopUninstall = { Result.success(UninstallOutcome.CANCELLED) }
            viewModel.onDesktopAppUninstallClicked(target)
            runCurrent()
            assertEquals(2, fakeExternalAppAction.desktopUninstallCalls)
            assertNull(expectMostRecentItem().snackbarMessage)
        }
    }

    @Test
    fun successfulDesktopRemovalRefreshesTheInventory() = runTest {
        val app = TestData.extendedApplicationsData.first()
        fakeApplicationsDataProvider.installedApplications = listOf(app)
        fakeExternalAppAction.desktopUninstall = {
            fakeApplicationsDataProvider.installedApplications = emptyList()
            Result.success(UninstallOutcome.REMOVED)
        }
        viewModel.uiStateFlow.test {
            assertEquals(listOf(app), awaitItem().applications)
            viewModel.onDesktopAppUninstallClicked(
                UninstallTarget.MacBundle("/Applications/Example.app")
            )
            runCurrent()
            assertTrue(expectMostRecentItem().applications.isEmpty())
        }
    }
}
