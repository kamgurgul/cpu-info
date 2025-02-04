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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.features.applications.ApplicationsScreen
import com.kgurgul.cpuinfo.features.information.InfoContainerScreen
import com.kgurgul.cpuinfo.features.processes.ProcessesScreen
import com.kgurgul.cpuinfo.features.settings.SettingsScreen
import com.kgurgul.cpuinfo.features.settings.licenses.LicensesScreen
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
import com.kgurgul.cpuinfo.ui.components.CpuNavigationSuiteScaffold
import com.kgurgul.cpuinfo.ui.components.CpuNavigationSuiteScaffoldDefault
import com.kgurgul.cpuinfo.utils.navigation.TopLevelRoute
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HostScreen(
    viewModel: HostViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    HostScreen(
        uiState = uiState,
    )
}

@Composable
fun HostScreen(
    uiState: HostViewModel.UiState,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val itemDefaultColors = CpuNavigationSuiteScaffoldDefault.itemDefaultColors()
    CpuNavigationSuiteScaffold(
        navigationSuiteItems = {
            buildTopLevelRoutes(
                isProcessesVisible = uiState.isProcessSectionVisible,
                isApplicationsVisible = uiState.isApplicationSectionVisible,
            ).forEach { topLevelRoute ->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(topLevelRoute.icon),
                            contentDescription = stringResource(topLevelRoute.name),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(topLevelRoute.name),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(topLevelRoute.route::class)
                    } == true,
                    onClick = {
                        navController.navigate(topLevelRoute.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
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
            startDestination = HostScreen.Information,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            composable<HostScreen.Information> {
                InfoContainerScreen()
            }
            composable<HostScreen.Applications> {
                ApplicationsScreen()
            }
            composable<HostScreen.Processes> {
                ProcessesScreen()
            }
            composable<HostScreen.Temperatures> {
                TemperatureScreen()
            }
            navigation<HostScreen.Settings>(
                startDestination = HostScreen.Settings.List,
            ) {
                composable<HostScreen.Settings.List> {
                    SettingsScreen(
                        onLicensesClicked = {
                            navController.navigate(HostScreen.Settings.Licenses)
                        }
                    )
                }
                composable<HostScreen.Settings.Licenses> {
                    LicensesScreen(
                        onNavigateBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Serializable
sealed interface HostScreen {
    @Serializable
    data object Information : HostScreen

    @Serializable
    data object Applications : HostScreen

    @Serializable
    data object Processes : HostScreen

    @Serializable
    data object Temperatures : HostScreen

    @Serializable
    data object Settings : HostScreen {
        @Serializable
        data object List : HostScreen

        @Serializable
        data object Licenses : HostScreen
    }
}

private fun buildTopLevelRoutes(
    isProcessesVisible: Boolean,
    isApplicationsVisible: Boolean,
) = buildList {
    add(
        TopLevelRoute(
            name = Res.string.hardware,
            route = HostScreen.Information,
            icon = Res.drawable.ic_cpu,
        ),
    )
    if (isApplicationsVisible) {
        add(
            TopLevelRoute(
                name = Res.string.applications,
                route = HostScreen.Applications,
                icon = Res.drawable.ic_android,
            ),
        )
    }
    if (isProcessesVisible) {
        add(
            TopLevelRoute(
                name = Res.string.processes,
                route = HostScreen.Processes,
                icon = Res.drawable.ic_process,
            ),
        )
    }
    add(
        TopLevelRoute(
            name = Res.string.temp,
            route = HostScreen.Temperatures,
            icon = Res.drawable.ic_temperature,
        ),
    )
    add(
        TopLevelRoute(
            name = Res.string.settings,
            route = HostScreen.Settings,
            icon = Res.drawable.ic_settings,
        ),
    )
}
