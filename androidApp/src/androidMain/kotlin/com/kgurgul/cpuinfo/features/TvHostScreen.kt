package com.kgurgul.cpuinfo.features

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
import com.kgurgul.cpuinfo.features.applications.TvApplicationsScreen
import com.kgurgul.cpuinfo.features.information.TvInfoContainerScreen
import com.kgurgul.cpuinfo.features.settings.TvSettingsScreen
import com.kgurgul.cpuinfo.features.temperature.TvTemperatureScreen
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.shared.ic_android
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temp
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
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
    NavigationDrawer(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(spacingMedium)
                    .selectableGroup(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement
                    .spacedBy(spacingSmall, Alignment.CenterVertically),
            ) {
                TvHostNavigationItem.bottomNavigationItems(
                    isApplicationsVisible = uiState.isApplicationSectionVisible,
                ).forEach { item ->
                    NavigationDrawerItem(
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
                        leadingContent = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = stringResource(item.label),
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
                            focusedContentColor = MaterialTheme.colorScheme.onBackground,
                            selectedContentColor = MaterialTheme.colorScheme.onBackground,
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceTint
                                .copy(alpha = 0.4f),
                            pressedContentColor = MaterialTheme.colorScheme.onBackground,
                            pressedContainerColor = MaterialTheme.colorScheme.surfaceTint
                                .copy(alpha = 0.4f),
                        ),
                    ) {
                        Text(
                            text = stringResource(item.label),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
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
            ) { TvSettingsScreen() }
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
                if (isApplicationsVisible) {
                    add(
                        HostNavigationItem(
                            label = Res.string.applications,
                            icon = Res.drawable.ic_android,
                            route = HostScreen.Applications.route,
                        ),
                    )
                }
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
