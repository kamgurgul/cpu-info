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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kgurgul.cpuinfo.features.applications.ApplicationsRoute
import com.kgurgul.cpuinfo.features.applications.applicationsScreen
import com.kgurgul.cpuinfo.features.information.InformationRoute
import com.kgurgul.cpuinfo.features.information.informationScreen
import com.kgurgul.cpuinfo.features.processes.ProcessesRoute
import com.kgurgul.cpuinfo.features.processes.processesScreen
import com.kgurgul.cpuinfo.features.settings.SettingsRoute
import com.kgurgul.cpuinfo.features.settings.settingsScreen
import com.kgurgul.cpuinfo.features.temperature.TemperaturesRoute
import com.kgurgul.cpuinfo.features.temperature.temperaturesScreen
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
            startDestination = InformationRoute,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            informationScreen()
            applicationsScreen()
            processesScreen()
            temperaturesScreen()
            settingsScreen(
                onLicensesClicked = {
                    navController.navigate(SettingsRoute.Licenses)
                },
                onNavigateBackClicked = {
                    navController.popBackStack()
                },
            )
        }
    }
}

private fun buildTopLevelRoutes(
    isProcessesVisible: Boolean,
    isApplicationsVisible: Boolean,
) = buildList {
    add(
        TopLevelRoute(
            name = Res.string.hardware,
            route = InformationRoute,
            icon = Res.drawable.ic_cpu,
        ),
    )
    if (isApplicationsVisible) {
        add(
            TopLevelRoute(
                name = Res.string.applications,
                route = ApplicationsRoute,
                icon = Res.drawable.ic_android,
            ),
        )
    }
    if (isProcessesVisible) {
        add(
            TopLevelRoute(
                name = Res.string.processes,
                route = ProcessesRoute,
                icon = Res.drawable.ic_process,
            ),
        )
    }
    add(
        TopLevelRoute(
            name = Res.string.temp,
            route = TemperaturesRoute,
            icon = Res.drawable.ic_temperature,
        ),
    )
    add(
        TopLevelRoute(
            name = Res.string.settings,
            route = SettingsRoute,
            icon = Res.drawable.ic_settings,
        ),
    )
}
