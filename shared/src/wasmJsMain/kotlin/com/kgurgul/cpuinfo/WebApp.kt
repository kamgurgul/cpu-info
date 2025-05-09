package com.kgurgul.cpuinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.ui.shouldUseDarkTheme
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import org.koin.compose.viewmodel.koinViewModel
import org.w3c.dom.Window

@OptIn(ExperimentalBrowserHistoryApi::class)
@Composable
fun WebApp(
    window: Window,
    hostViewModel: HostViewModel = koinViewModel(),
) {
    val uiState by hostViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)
    val navController = rememberNavController()
    CpuInfoTheme(
        useDarkTheme = darkTheme,
    ) {
        HostScreen(
            viewModel = hostViewModel,
            navController = navController,
        )
    }
    LaunchedEffect(Unit) {
        window.bindToNavigation(navController)
    }
}
