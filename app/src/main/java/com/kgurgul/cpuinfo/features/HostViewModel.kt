package com.kgurgul.cpuinfo.features

import androidx.lifecycle.ViewModel
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor(
    processesDataObservable: ProcessesDataObservable,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        UiState(
            isProcessSectionVisible = processesDataObservable.areProcessesSupported()
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    data class UiState(
        val isProcessSectionVisible: Boolean = false,
    )
}