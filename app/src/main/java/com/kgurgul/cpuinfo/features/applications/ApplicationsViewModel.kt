package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.utils.SingleLiveEvent
import com.kgurgul.cpuinfo.utils.wrappers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val applicationsDataObservable: ApplicationsDataObservable,
    private val getPackageNameInteractor: GetPackageNameInteractor,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event> = _events.asLiveData()

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
                _uiStateFlow.update { it.copy(snackbarMessage = R.string.cpu_open) }
            } else {
                _events.value = Event.OpenApp(packageName)
            }
        }
    }

    fun onCannotOpenApp() {
        _uiStateFlow.update { it.copy(snackbarMessage = R.string.app_open) }
    }

    fun onSnackbarDismissed() {
        _uiStateFlow.update { it.copy(snackbarMessage = -1) }
    }

    fun onAppSettingsClicked(id: String) {
        _events.value = Event.OpenAppSettings(id)
    }

    fun onAppUninstallClicked(id: String) {
        viewModelScope.launch {
            if (getPackageNameInteractor.invoke(Unit) == id) {
                _uiStateFlow.update { it.copy(snackbarMessage = R.string.cpu_uninstall) }
            } else {
                _events.value = Event.UninstallApp(id)
            }
        }
    }

    fun onNativeLibsClicked(nativeLibraryDir: String) {
        val nativeDirFile = File(nativeLibraryDir)
        val libs = nativeDirFile.listFiles()?.map { it.name } ?: emptyList()
        if (libs.isNotEmpty()) {
            _events.value = Event.ShowNativeLibraries(libs)
        }
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

    sealed interface Event {
        data class OpenApp(val packageName: String) : Event
        data class OpenAppSettings(val packageName: String) : Event
        data class UninstallApp(val packageName: String) : Event
        data class ShowNativeLibraries(val nativeLibs: List<String>) : Event
    }

    data class UiState(
        val isLoading: Boolean = false,
        val withSystemApps: Boolean = false,
        val isSortAscending: Boolean = true,
        val applications: ImmutableList<ExtendedApplicationData> = persistentListOf(),
        val snackbarMessage: Int = -1,
    )
}