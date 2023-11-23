package com.kgurgul.cpuinfo.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.applications.ApplicationsScreen
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoScreen
import com.kgurgul.cpuinfo.features.processes.ProcessesScreen
import com.kgurgul.cpuinfo.features.settings.SettingsScreen
import com.kgurgul.cpuinfo.features.temperature.TemperatureScreen
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
                HostNavigationItem.bottomNavigationItems(
                    isProcessesVisible = uiState.isProcessSectionVisible,
                ).forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.iconRes),
                                contentDescription = stringResource(item.labelRes),
                            )
                        },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HostScreen.Hardware.route,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            composable(HostScreen.Hardware.route) { HardwareInfoScreen() }
            composable(HostScreen.Applications.route) { ApplicationsScreen() }
            composable(HostScreen.Processes.route) { ProcessesScreen() }
            composable(HostScreen.Temperatures.route) { TemperatureScreen() }
            composable(HostScreen.Settings.route) { SettingsScreen() }
        }
    }
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

    companion object {
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