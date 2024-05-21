package com.kgurgul.cpuinfo.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val withLabel = !uiState.isProcessSectionVisible
    val defaultItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            selectedIndicatorColor = MaterialTheme.colorScheme.secondary,
            unselectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledIconColor = MaterialTheme.colorScheme.onPrimary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationRailItemColors = NavigationRailItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            selectedIndicatorColor = MaterialTheme.colorScheme.secondary,
            unselectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledIconColor = MaterialTheme.colorScheme.onPrimary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    )
    val navigationSuitColors = NavigationSuiteDefaults.colors(
        navigationBarContainerColor = MaterialTheme.colorScheme.primary,
        navigationBarContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationRailContainerColor = MaterialTheme.colorScheme.primary,
        navigationRailContentColor = MaterialTheme.colorScheme.onPrimary,
    )
    NavigationSuiteScaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        navigationSuiteColors = navigationSuitColors,
        navigationSuiteItems = {
            HostNavigationItem.bottomNavigationItems(
                isProcessesVisible = uiState.isProcessSectionVisible,
            ).forEach { item ->
                item(
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
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = defaultItemColors,
                )
            }
        },
        modifier = Modifier.semantics {
            testTagsAsResourceId = true
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = HostScreen.Information.route,
            modifier = Modifier.systemBarsPadding(),
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