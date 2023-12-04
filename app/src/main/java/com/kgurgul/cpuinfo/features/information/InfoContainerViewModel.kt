package com.kgurgul.cpuinfo.features.information

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoContainerViewModel @Inject constructor(
    private val ramCleanupAction: RamCleanupAction,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        UiState(
            isRamCleanupVisible = Build.VERSION.SDK_INT < 24,
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onClearRamClicked() {
        viewModelScope.launch { ramCleanupAction(Unit) }
    }

    data class UiState(
        val isRamCleanupVisible: Boolean = false,
    )
}