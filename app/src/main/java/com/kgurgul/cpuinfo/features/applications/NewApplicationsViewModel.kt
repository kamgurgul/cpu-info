package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
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
            .onEach(::handleApplicationsResult)
            .launchIn(viewModelScope)
        onRefreshApplications()
    }

    fun onRefreshApplications() {
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

    private fun handleApplicationsResult(result: Result<List<ExtendedApplicationData>>) {
        _uiStateFlow.update {
            it.copy(
                isLoading = result is Result.Loading,
                applications = if (result is Result.Success) {
                    result.data.toImmutableList()
                } else {
                    it.applications
                }
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val applications: ImmutableList<ExtendedApplicationData> = persistentListOf(),
        val snackbarMessage: Int = -1,
        val revealedCardId: String? = null,
    )
}