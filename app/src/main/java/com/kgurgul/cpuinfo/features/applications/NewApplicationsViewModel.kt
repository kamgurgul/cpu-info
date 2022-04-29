package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.utils.wrappers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewApplicationsViewModel @Inject constructor(
    private val applicationsDataObservable: ApplicationsDataObservable,
    private val getPackageNameInteractor: GetPackageNameInteractor,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        applicationsDataObservable.observe()
            .onEach { result ->
                _uiStateFlow.update { it.copy(applicationsResult = result) }
            }
            .launchIn(viewModelScope)
        refreshApplications()
    }

    fun refreshApplications() {
        applicationsDataObservable.invoke(
            ApplicationsDataObservable.Params(
                withSystemApps = false,
                sortOrderFromBoolean(true)
            )
        )
    }

    fun onApplicationClicked(packageName: String) {
        viewModelScope.launch {
            if (getPackageNameInteractor.invoke(Unit) == packageName) {
                _uiStateFlow.update { it.copy(snackbarMessage = R.string.cpu_open) }
            }
        }
    }

    fun onSnackbarDismissed() {
        _uiStateFlow.update { it.copy(snackbarMessage = -1) }
    }

    data class UiState(
        val applicationsResult: Result<List<ExtendedApplicationData>> = Result.Loading,
        val snackbarMessage: Int = -1
    )
}