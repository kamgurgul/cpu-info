package com.kgurgul.cpuinfo.features

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.features.applications.TvApplicationsScreen
import com.kgurgul.cpuinfo.features.information.TvInfoContainerScreen
import com.kgurgul.cpuinfo.features.settings.SettingsScreen
import com.kgurgul.cpuinfo.features.temperature.TvTemperatureScreen
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temp
import com.kgurgul.cpuinfo.ui.components.CpuNavigationSuiteScaffold
import com.kgurgul.cpuinfo.ui.components.CpuNavigationSuiteScaffoldDefault
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvHostScreen(
    viewModel: HostViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvHostScreen(
        uiState = uiState,
    )
}

@Composable
fun TvHostScreen(
    uiState: HostViewModel.UiState,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val itemDefaultColors = CpuNavigationSuiteScaffoldDefault.itemDefaultColors()
    CpuNavigationSuiteScaffold(
        navigationSuiteItems = {
            TvHostNavigationItem.bottomNavigationItems(
                isApplicationsVisible = uiState.isApplicationSectionVisible,
            ).forEach { item ->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = stringResource(item.label),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(item.label),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = currentDestination?.hierarchy
                        ?.any { it.route == item.route } == true,
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
                    },
                    colors = itemDefaultColors,
                )
            }
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = TvHostScreen.Information.route,
        ) {
            composable(
                route = TvHostScreen.Information.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { TvInfoContainerScreen() }
            composable(
                route = TvHostScreen.Applications.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { TvApplicationsScreen() }
            composable(
                route = TvHostScreen.Temperatures.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { TvTemperatureScreen() }
            composable(
                route = TvHostScreen.Settings.route,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() },
            ) { SettingsScreen() }
        }
    }
}

sealed class TvHostScreen(val route: String) {
    data object Information : TvHostScreen("information_route")
    data object Applications : TvHostScreen("applications_route")
    data object Temperatures : TvHostScreen("temperatures_route")
    data object Settings : TvHostScreen("settings_route")
}

data class TvHostNavigationItem(
    val label: StringResource,
    val icon: DrawableResource,
    val route: String,
) {

    companion object {
        fun bottomNavigationItems(
            isApplicationsVisible: Boolean,
        ): List<HostNavigationItem> {
            return buildList {
                add(
                    HostNavigationItem(
                        label = Res.string.hardware,
                        icon = Res.drawable.ic_cpu,
                        route = HostScreen.Information.route,
                    ),
                )
                /*if (isApplicationsVisible) {
                    add(
                        HostNavigationItem(
                            label = Res.string.applications,
                            icon = Res.drawable.ic_android,
                            route = HostScreen.Applications.route,
                        ),
                    )
                }*/
                add(
                    HostNavigationItem(
                        label = Res.string.temp,
                        icon = Res.drawable.ic_temperature,
                        route = HostScreen.Temperatures.route,
                    ),
                )
                add(
                    HostNavigationItem(
                        label = Res.string.settings,
                        icon = Res.drawable.ic_settings,
                        route = HostScreen.Settings.route,
                    ),
                )
            }
        }
    }
}
