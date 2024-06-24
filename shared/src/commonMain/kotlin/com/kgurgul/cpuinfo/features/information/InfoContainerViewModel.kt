package com.kgurgul.cpuinfo.features.information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InfoContainerViewModel(
    private val ramCleanupAction: RamCleanupAction,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onPageChanged(index: Int) {
        _uiStateFlow.update {
            it.copy(
                isRamCleanupVisible = ramCleanupAction.isCleanupActionAvailable()
                        && index == RAM_POS
            )
        }
    }

    fun onClearRamClicked() {
        viewModelScope.launch { ramCleanupAction(Unit) }
    }

    data class UiState(
        val isRamCleanupVisible: Boolean = false,
    )

    companion object {
        const val CPU_POS = 0
        const val GPU_POS = 1
        const val RAM_POS = 2
        const val STORAGE_POS = 3
        const val SCREEN_POS = 4
        const val ANDROID_POS = 5
        const val HARDWARE_POS = 6
        const val SENSORS_POS = 7

        const val INFO_PAGE_AMOUNT = 8
    }
}