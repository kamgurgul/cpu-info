package com.kgurgul.cpuinfo.features.applications

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.data.provider.IApplicationsDataProvider
import com.kgurgul.cpuinfo.domain.action.IExternalAppAction
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.FilterApplicationsInteractor
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_open
import com.kgurgul.cpuinfo.shared.cpu_open
import com.kgurgul.cpuinfo.shared.cpu_uninstall
import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class ApplicationsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val applicationsDataObservable: ApplicationsDataObservable,
    private val getPackageNameInteractor: GetPackageNameInteractor,
    private val userPreferencesRepository: IUserPreferencesRepository,
    private val externalAppAction: IExternalAppAction,
    private val filterApplicationsInteractor: FilterApplicationsInteractor,
    private val applicationsDataProvider: IApplicationsDataProvider,
) : ViewModel() {

    private val localDataFlow = MutableStateFlow(LocalUiState())
    private val cachedApplications = mutableListOf<ExtendedApplicationData>()
    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
        .onEach { userPreferences ->
            applicationsDataObservable.invoke(
                ApplicationsDataObservable.Params(
                    withSystemApps = userPreferences.withSystemApps,
                    sortOrderFromBoolean(userPreferences.isApplicationsSortingAscending),
                ),
            )
        }
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY_KEY, initialValue = "")
    private val debouncedSearchQueryFlow = searchQuery.mapLatest { query ->
        if (query.isNotEmpty()) {
            delay(SEARCH_DEBOUNCE_MS)
        }
        query
    }
    val uiStateFlow = combine(
        localDataFlow,
        userPreferencesFlow,
        applicationsDataObservable.observe(),
        debouncedSearchQueryFlow,
    ) { localData, userPreferences, applicationsResult, searchQuery ->
        if (applicationsResult is Result.Success) {
            cachedApplications.clear()
            cachedApplications.addAll(applicationsResult.data)
        }
        val filteredApplications = if (searchQuery.isEmpty()) {
            cachedApplications
        } else {
            filterApplicationsInteractor.invoke(
                FilterApplicationsInteractor.Params(cachedApplications, searchQuery)
            )
        }
        UiState(
            isLoading = applicationsResult is Result.Loading,
            withSystemApps = userPreferences.withSystemApps,
            isSortAscending = userPreferences.isApplicationsSortingAscending,
            isDialogVisible = localData.isDialogVisible,
            nativeLibs = localData.nativeLibs,
            applications = filteredApplications.toImmutableList(),
            snackbarMessage = localData.snackbarMessage,
            hasSystemAppsFiltering = applicationsDataProvider.hasSystemAppsFiltering(),
            hasAppManagement = applicationsDataProvider.hasAppManagementSupported(),
            hasManualRefresh = applicationsDataProvider.hasManualRefresh(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun onRefreshApplications() {
        val currentUiState = uiStateFlow.value
        applicationsDataObservable.invoke(
            ApplicationsDataObservable.Params(
                withSystemApps = currentUiState.withSystemApps,
                sortOrderFromBoolean(currentUiState.isSortAscending),
            ),
        )
    }

    fun onApplicationClicked(packageName: String) {
        viewModelScope.launch {
            if (getPackageNameInteractor.invoke(Unit) == packageName) {
                localDataFlow.update { it.copy(snackbarMessage = Res.string.cpu_open) }
            } else {
                externalAppAction.launch(packageName)
                    .onFailure {
                        localDataFlow.update { it.copy(snackbarMessage = Res.string.app_open) }
                    }
            }
        }
    }

    fun onSnackbarDismissed() {
        localDataFlow.update { it.copy(snackbarMessage = null) }
    }

    fun onAppSettingsClicked(id: String) {
        externalAppAction.openSettings(id)
    }

    fun onAppUninstallClicked(id: String) {
        viewModelScope.launch {
            if (getPackageNameInteractor.invoke(Unit) == id) {
                localDataFlow.update { it.copy(snackbarMessage = Res.string.cpu_uninstall) }
            } else {
                externalAppAction.uninstall(id)
            }
        }
    }

    fun onNativeLibsClicked(libs: List<String>) {
        if (libs.isNotEmpty()) {
            localDataFlow.update {
                it.copy(
                    isDialogVisible = true,
                    nativeLibs = libs.toImmutableList(),
                )
            }
        }
    }

    fun onNativeLibsDialogDismissed() {
        localDataFlow.update {
            it.copy(
                isDialogVisible = false,
                nativeLibs = persistentListOf(),
            )
        }
    }

    fun onNativeLibsNameClicked(name: String) {
        externalAppAction.searchOnWeb(name)
    }

    fun onSystemAppsSwitched(checked: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setApplicationsWithSystemApps(checked)
        }
    }

    fun onSortOrderChange(isAscending: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setApplicationsSortingOrder(isAscending)
        }
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY_KEY] = query
    }

    data class LocalUiState(
        val isDialogVisible: Boolean = false,
        val nativeLibs: ImmutableList<String> = persistentListOf(),
        val snackbarMessage: StringResource? = null,
    )

    @Stable
    data class UiState(
        val isLoading: Boolean = false,
        val withSystemApps: Boolean = false,
        val isSortAscending: Boolean = true,
        val isDialogVisible: Boolean = false,
        val nativeLibs: ImmutableList<String> = persistentListOf(),
        val applications: ImmutableList<ExtendedApplicationData> = persistentListOf(),
        val snackbarMessage: StringResource? = null,
        val hasSystemAppsFiltering: Boolean = false,
        val hasAppManagement: Boolean = false,
        val hasManualRefresh: Boolean = false,
    )
}

private const val SEARCH_QUERY_KEY = "searchQuery"
private const val SEARCH_DEBOUNCE_MS = 300L
