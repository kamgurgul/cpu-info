package com.kgurgul.cpuinfo.features

import android.os.Build
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor() : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        UiState(
            isProcessSectionVisible = Build.VERSION.SDK_INT <= Build.VERSION_CODES.M
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    data class UiState(
        val isProcessSectionVisible: Boolean = false,
    )
}