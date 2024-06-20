package com.kgurgul.cpuinfo.features

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.features.applications.ApplicationsScreen
import com.kgurgul.cpuinfo.features.information.InfoContainerScreen
import com.kgurgul.cpuinfo.features.processes.ProcessesScreen
import com.kgurgul.cpuinfo.features.settings.SettingsScreen
import com.kgurgul.cpuinfo.features.temperature.TemperatureScreen
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.shared.ic_android
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_process
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.processes
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HostScreen(
    viewModel: HostViewModel = koinViewModel()
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
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val withLabel = !uiState.isProcessSectionVisible
                HostNavigationItem.bottomNavigationItems(
                    isProcessesVisible = uiState.isProcessSectionVisible,
                ).forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = stringResource(item.label),
                            )
                        },
                        label = if (withLabel) {
                            {
                                Text(
                                    text = stringResource(item.label),
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        } else null,
                        selected = currentDestination?.hierarchy
                            ?.any { it.route == item.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.findStartDestination().route?.let {
                                    popUpTo(it) {
                                        saveState = true
                                    }
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
            startDestination = HostScreen.Information.route,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {
            composable(
                route = HostScreen.Information.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { InfoContainerScreen() }
            composable(
                route = HostScreen.Applications.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { ApplicationsScreen() }
            composable(
                route = HostScreen.Processes.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { ProcessesScreen() }
            composable(
                route = HostScreen.Temperatures.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { TemperatureScreen() }
            composable(
                route = HostScreen.Settings.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { SettingsScreen() }
        }
    }
}

sealed class HostScreen(val route: String) {
    data object Information : HostScreen("information_route")
    data object Applications : HostScreen("applications_route")
    data object Processes : HostScreen("processes_route")
    data object Temperatures : HostScreen("temperatures_route")
    data object Settings : HostScreen("settings_route")
}

data class HostNavigationItem(
    val label: StringResource,
    val icon: DrawableResource,
    val route: String,
) {

    companion object {
        fun bottomNavigationItems(isProcessesVisible: Boolean): List<HostNavigationItem> {
            return buildList {
                add(
                    HostNavigationItem(
                        label = Res.string.hardware,
                        icon = Res.drawable.ic_cpu,
                        route = HostScreen.Information.route,
                    )
                )
                add(
                    HostNavigationItem(
                        label = Res.string.applications,
                        icon = Res.drawable.ic_android,
                        route = HostScreen.Applications.route,
                    )
                )
                if (isProcessesVisible) {
                    add(
                        HostNavigationItem(
                            label = Res.string.processes,
                            icon = Res.drawable.ic_process,
                            route = HostScreen.Processes.route,
                        )
                    )
                }
                add(
                    HostNavigationItem(
                        label = Res.string.temp,
                        icon = Res.drawable.ic_temperature,
                        route = HostScreen.Temperatures.route,
                    )
                )
                add(
                    HostNavigationItem(
                        label = Res.string.settings,
                        icon = Res.drawable.ic_settings,
                        route = HostScreen.Settings.route,
                    )
                )
            }
        }
    }
}
