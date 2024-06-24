package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.action.ExternalAppAction
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_open
import com.kgurgul.cpuinfo.shared.cpu_open
import com.kgurgul.cpuinfo.shared.cpu_uninstall
import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class ApplicationsViewModel(
    private val applicationsDataObservable: ApplicationsDataObservable,
    private val getPackageNameInteractor: GetPackageNameInteractor,
    private val userPreferencesRepository: IUserPreferencesRepository,
    private val externalAppAction: ExternalAppAction,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        userPreferencesRepository.userPreferencesFlow
            .onEach { userPreferences ->
                _uiStateFlow.update {
                    it.copy(
                        withSystemApps = userPreferences.withSystemApps,
                        isSortAscending = userPreferences.isApplicationsSortingAscending,
                    )
                }
                onRefreshApplications()
            }
            .launchIn(viewModelScope)
        applicationsDataObservable.observe()
            .onEach(::handleApplicationsResult)
            .launchIn(viewModelScope)
    }

    fun onRefreshApplications() {
        val currentUiState = _uiStateFlow.value
        applicationsDataObservable.invoke(
            ApplicationsDataObservable.Params(
                withSystemApps = currentUiState.withSystemApps,
                sortOrderFromBoolean(currentUiState.isSortAscending)
            )
        )
    }

    fun onApplicationClicked(packageName: String) {
        viewModelScope.launch {
            if (getPackageNameInteractor.invoke(Unit) == packageName) {
                _uiStateFlow.update { it.copy(snackbarMessage = Res.string.cpu_open) }
            } else {
                externalAppAction.launch(packageName)
                    .onFailure {
                        _uiStateFlow.update { it.copy(snackbarMessage = Res.string.app_open) }
                    }
            }
        }
    }

    fun onSnackbarDismissed() {
        _uiStateFlow.update { it.copy(snackbarMessage = null) }
    }

    fun onAppSettingsClicked(id: String) {
        externalAppAction.openSettings(id)
    }

    fun onAppUninstallClicked(id: String) {
        viewModelScope.launch {
            if (getPackageNameInteractor.invoke(Unit) == id) {
                _uiStateFlow.update { it.copy(snackbarMessage = Res.string.cpu_uninstall) }
            } else {
                externalAppAction.uninstall(id)
            }
        }
    }

    fun onNativeLibsClicked(libs: List<String>) {
        if (libs.isNotEmpty()) {
            _uiStateFlow.update {
                it.copy(
                    isDialogVisible = true,
                    nativeLibs = libs.toImmutableList()
                )
            }
        }
    }

    fun onNativeLibsDialogDismissed() {
        _uiStateFlow.update {
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

    private fun handleApplicationsResult(result: Result<List<ExtendedApplicationData>>) {
        _uiStateFlow.update {
            it.copy(
                isLoading = result is Result.Loading,
                applications = if (result is Result.Success) {
                    result.data.toImmutableList()
                } else {
                    it.applications
                },
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val withSystemApps: Boolean = false,
        val isSortAscending: Boolean = true,
        val isDialogVisible: Boolean = false,
        val nativeLibs: ImmutableList<String> = persistentListOf(),
        val applications: ImmutableList<ExtendedApplicationData> = persistentListOf(),
        val snackbarMessage: StringResource? = null,
    )
}