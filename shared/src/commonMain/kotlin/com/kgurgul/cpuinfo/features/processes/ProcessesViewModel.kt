package com.kgurgul.cpuinfo.features.processes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.domain.observe
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ProcessesViewModel(
    processesDataObservable: ProcessesDataObservable,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        processesDataObservable.observe()
            .onEach(::handleProcessesResult)
            .launchIn(viewModelScope)
    }

    private fun handleProcessesResult(processes: List<ProcessItem>) {
        _uiStateFlow.update { it.copy(isLoading = false, processes = processes.toPersistentList()) }
    }

    data class UiState(
        val isLoading: Boolean = true,
        val processes: ImmutableList<ProcessItem> = persistentListOf(),
    )
}