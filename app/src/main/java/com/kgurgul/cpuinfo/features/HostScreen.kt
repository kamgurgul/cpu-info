package com.kgurgul.cpuinfo.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Composable
fun HostScreen(
    viewModel: HostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    HostScreen(
        uiState = uiState,
    )
}

@Composable
fun HostScreen(
    uiState: HostViewModel.UiState
) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                /*items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }*/
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HostScreen.Hardware.route,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            composable(HostScreen.Hardware.route) {
            }
            composable(HostScreen.Applications.route) {
            }
            composable(HostScreen.Processes.route) {
            }
            composable(HostScreen.Temperatures.route) {
            }
            composable(HostScreen.Settings.route) {
            }
        }
    }
    uiState.isProcessSectionVisible
}

sealed class HostScreen(val route: String) {
    data object Hardware : HostScreen("hardware_route")
    data object Applications : HostScreen("applications_route")
    data object Processes : HostScreen("processes_route")
    data object Temperatures : HostScreen("temperatures_route")
    data object Settings : HostScreen("settings_route")
}

data class HostNavigationItem(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    val route: String,
) {

    fun bottomNavigationItems(isProcessesVisible: Boolean): List<HostNavigationItem> {
        return buildList {
            add(
                HostNavigationItem(
                    labelRes = R.string.hardware,
                    iconRes = R.drawable.ic_hardware,
                    route = HostScreen.Hardware.route,
                )
            )
            add(
                HostNavigationItem(
                    labelRes = R.string.applications,
                    iconRes = R.drawable.ic_android,
                    route = HostScreen.Applications.route,
                )
            )
            if (isProcessesVisible) {
                add(
                    HostNavigationItem(
                        labelRes = R.string.processes,
                        iconRes = R.drawable.ic_process,
                        route = HostScreen.Processes.route,
                    )
                )
            }
            add(
                HostNavigationItem(
                    labelRes = R.string.temp,
                    iconRes = R.drawable.ic_temperature,
                    route = HostScreen.Temperatures.route,
                )
            )
            add(
                HostNavigationItem(
                    labelRes = R.string.settings,
                    iconRes = R.drawable.ic_settings,
                    route = HostScreen.Settings.route,
                )
            )
        }
    }
}

@Preview
@Composable
fun HostScreenPreview() {
    CpuInfoTheme {
        HostScreen(
            uiState = HostViewModel.UiState(),
        )
    }
}