package com.kgurgul.cpuinfo.features.settings.licenses

import androidx.lifecycle.ViewModel

class LicensesViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
    )
}
