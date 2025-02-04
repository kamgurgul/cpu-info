package com.kgurgul.cpuinfo.tv.features

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
import androidx.navigation.NavDestination.Companion.hasRoute
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
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.shared.ic_android
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temp
import com.kgurgul.cpuinfo.tv.features.applications.TvApplicationsScreen
import com.kgurgul.cpuinfo.tv.features.information.TvInfoContainerScreen
import com.kgurgul.cpuinfo.tv.features.settings.TvSettingsScreen
import com.kgurgul.cpuinfo.tv.features.temperature.TvTemperatureScreen
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.navigation.TopLevelRoute
import kotlinx.serialization.Serializable
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
                buildTopLevelRoutes(
                    isApplicationsVisible = uiState.isApplicationSectionVisible,
                ).forEach { topLevelRoute ->
                    NavigationDrawerItem(
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
                        leadingContent = {
                            Icon(
                                painter = painterResource(topLevelRoute.icon),
                                contentDescription = stringResource(topLevelRoute.name),
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
                            text = stringResource(topLevelRoute.name),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = TvHostScreen.Information,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            composable<TvHostScreen.Information> {
                TvInfoContainerScreen()
            }
            composable<TvHostScreen.Applications> {
                TvApplicationsScreen()
            }
            composable<TvHostScreen.Temperatures> {
                TvTemperatureScreen()
            }
            composable<TvHostScreen.Settings> {
                TvSettingsScreen()
            }
        }
    }
}

@Serializable
sealed interface TvHostScreen {
    @Serializable
    data object Information : TvHostScreen

    @Serializable
    data object Applications : TvHostScreen

    @Serializable
    data object Temperatures : TvHostScreen

    @Serializable
    data object Settings : TvHostScreen
}

private fun buildTopLevelRoutes(
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
