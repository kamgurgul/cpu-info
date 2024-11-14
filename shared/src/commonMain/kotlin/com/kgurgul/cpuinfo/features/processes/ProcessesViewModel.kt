package com.kgurgul.cpuinfo.features.processes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProcessesViewModel(
    processesDataObservable: ProcessesDataObservable,
    private val userPreferencesRepository: IUserPreferencesRepository,
) : ViewModel() {

    val uiStateFlow = userPreferencesRepository.userPreferencesFlow
        .flatMapLatest { userPreferences ->
            val isSortAscending = userPreferences.isProcessesSortingAscending
            processesDataObservable.observe(
                ProcessesDataObservable.Params(
                    sortOrder = sortOrderFromBoolean(isSortAscending),
                ),
            ).map { processes ->
                isSortAscending to processes
            }
        }.map {
            val (isSortAscending, processes) = it
            UiState(
                isLoading = false,
                processes = processes.toImmutableList(),
                isSortAscending = isSortAscending,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun onSortOrderChange(isAscending: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setProcessesSortingOrder(isAscending)
        }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val processes: ImmutableList<ProcessItem> = persistentListOf(),
        val isSortAscending: Boolean = true,
    )
}
