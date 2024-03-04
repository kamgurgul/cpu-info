package com.kgurgul.cpuinfo.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.applications.ApplicationsScreen
import com.kgurgul.cpuinfo.features.information.InfoContainerScreen
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

@OptIn(ExperimentalComposeUiApi::class)
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
                                painter = painterResource(item.iconRes),
                                contentDescription = stringResource(item.labelRes),
                            )
                        },
                        label = if (withLabel) {
                            {
                                Text(
                                    text = stringResource(item.labelRes),
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
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
        }
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
                        iconRes = R.drawable.ic_cpu,
                        route = HostScreen.Information.route,
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