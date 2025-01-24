package com.kgurgul.cpuinfo.features.settings.licenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.domain.result.GetLicensesInteractor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class LicensesViewModel(
    getLicensesInteractor: GetLicensesInteractor,
) : ViewModel() {

    val uiStateFlow = flow {
        emit(UiState(isLoading = true))
        getLicensesInteractor(Unit)
            .onSuccess {
                emit(UiState(isLoading = false, licenses = it.toImmutableList()))
            }
            .onFailure {
                emit(UiState(isLoading = false, isError = true))
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    data class UiState(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val licenses: ImmutableList<License> = persistentListOf(),
    )
}
