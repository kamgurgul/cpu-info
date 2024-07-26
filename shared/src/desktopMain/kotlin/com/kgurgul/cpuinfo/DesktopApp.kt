package com.kgurgul.cpuinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.ui.shouldUseDarkTheme
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DesktopApp(
    hostViewModel: HostViewModel = koinViewModel()
) {
    val uiState by hostViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)
    CpuInfoTheme(
        useDarkTheme = darkTheme
    ) {
        HostScreen(hostViewModel)
    }
}
