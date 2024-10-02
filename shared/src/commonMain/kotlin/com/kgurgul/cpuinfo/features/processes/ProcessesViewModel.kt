package com.kgurgul.cpuinfo.features.processes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProcessesViewModel(
    processesDataObservable: ProcessesDataObservable,
    private val userPreferencesRepository: IUserPreferencesRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        userPreferencesRepository.userPreferencesFlow
            .flatMapLatest { userPreferences ->
                _uiStateFlow.update {
                    it.copy(isSortAscending = userPreferences.isProcessesSortingAscending)
                }
                processesDataObservable.observe(
                    ProcessesDataObservable.Params(
                        sortOrder = sortOrderFromBoolean(
                            userPreferences.isProcessesSortingAscending,
                        ),
                    ),
                )
            }.onEach(::handleProcessesResult)
            .launchIn(viewModelScope)
    }

    fun onSortOrderChange(isAscending: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setProcessesSortingOrder(isAscending)
        }
    }

    private fun handleProcessesResult(processes: List<ProcessItem>) {
        _uiStateFlow.update { it.copy(isLoading = false, processes = processes.toPersistentList()) }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val processes: ImmutableList<ProcessItem> = persistentListOf(),
        val isSortAscending: Boolean = true,
    )
}
