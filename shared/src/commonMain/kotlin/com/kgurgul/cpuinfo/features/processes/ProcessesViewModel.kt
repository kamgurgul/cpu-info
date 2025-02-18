package com.kgurgul.cpuinfo.features.processes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.domain.result.FilterProcessesInteractor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProcessesViewModel(
    processesDataObservable: ProcessesDataObservable,
    private val savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: IUserPreferencesRepository,
    private val filterProcessesInteractor: FilterProcessesInteractor,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY_KEY, initialValue = "")
    private val debouncedSearchQueryFlow = searchQuery.mapLatest { query ->
        if (query.isNotEmpty()) {
            delay(SEARCH_DEBOUNCE_MS)
        }
        query
    }
    val uiStateFlow = userPreferencesRepository.userPreferencesFlow
        .flatMapLatest { userPreferences ->
            val isSortAscending = userPreferences.isProcessesSortingAscending
            combine(
                processesDataObservable.observe(
                    ProcessesDataObservable.Params(
                        sortOrder = sortOrderFromBoolean(isSortAscending),
                    ),
                ),
                debouncedSearchQueryFlow,
            ) { processes, searchQuery ->
                val filteredProcesses = if (searchQuery.isEmpty()) {
                    processes
                } else {
                    filterProcessesInteractor(
                        FilterProcessesInteractor.Params(
                            processes = processes,
                            searchQuery = searchQuery,
                        ),
                    )
                }
                isSortAscending to filteredProcesses
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

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY_KEY] = query
    }

    data class UiState(
        val isLoading: Boolean = true,
        val processes: ImmutableList<ProcessItem> = persistentListOf(),
        val isSortAscending: Boolean = true,
    )
}

private const val SEARCH_QUERY_KEY = "searchQuery"
private const val SEARCH_DEBOUNCE_MS = 300L
