/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.kgurgul.cpuinfo.features.applications.ApplicationsRoute
import com.kgurgul.cpuinfo.features.applications.applicationsScreen
import com.kgurgul.cpuinfo.features.information.InformationRoute
import com.kgurgul.cpuinfo.features.information.informationScreen
import com.kgurgul.cpuinfo.features.processes.ProcessesRoute
import com.kgurgul.cpuinfo.features.processes.processesScreen
import com.kgurgul.cpuinfo.features.settings.LicensesRoute
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
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.navigation.LocalNativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.LocalNavigationBarPadding
import com.kgurgul.cpuinfo.utils.navigation.NativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.NativeNavigationTab
import com.kgurgul.cpuinfo.utils.navigation.NavigationState
import com.kgurgul.cpuinfo.utils.navigation.Navigator
import com.kgurgul.cpuinfo.utils.navigation.TopLevelRoute
import com.kgurgul.cpuinfo.utils.navigation.navTransitionSpecs
import com.kgurgul.cpuinfo.utils.navigation.toEntries
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HostScreen(
    viewModel: HostViewModel = koinViewModel(),
    navigationState: NavigationState = rememberHostNavigationState(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    HostScreen(
        uiState = uiState,
        navigationState = navigationState,
    )
}

@Composable
fun HostScreen(
    uiState: HostViewModel.UiState,
    navigationState: NavigationState = rememberHostNavigationState(),
) {
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val entryProvider =
        remember(navigator) {
            entryProvider<NavKey> {
                informationScreen()
                applicationsScreen()
                processesScreen()
                temperaturesScreen()
                settingsScreen(
                    onLicensesClicked = { navigator.navigate(LicensesRoute) },
                    onNavigateBackClicked = { navigator.goBack() },
                )
            }
        }
    val topLevelRoutes =
        buildTopLevelRoutes(
            isProcessesVisible = uiState.isProcessSectionVisible,
            isApplicationsVisible = uiState.isApplicationSectionVisible,
        )
    val navTransitions = navTransitionSpecs(navigator.isBottomBarNavigation)
    val content =
        @Composable {
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                transitionSpec = navTransitions.transitionSpec,
                popTransitionSpec = navTransitions.popTransitionSpec,
                predictivePopTransitionSpec = navTransitions.predictivePopTransitionSpec,
            )
        }
    val nativeNavigation = LocalNativeNavigation.current
    if (nativeNavigation != null) {
        NativeNavigationHost(
            nativeNavigation = nativeNavigation,
            topLevelRoutes = topLevelRoutes,
            selectedRoute = navigationState.topLevelRoute,
            onRouteSelected = { navigator.navigate(it, fromBottomBar = true) },
            content = content,
        )
        return
    }
    val itemColors = CpuNavigationSuiteScaffoldDefault.itemColors()
    CpuNavigationSuiteScaffold(
        navigationItems = {
            topLevelRoutes.forEach { topLevelRoute ->
                NavigationSuiteItem(
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
                    selected = topLevelRoute.route == navigationState.topLevelRoute,
                    onClick = { navigator.navigate(topLevelRoute.route, fromBottomBar = true) },
                    colors = itemColors,
                )
            }
        },
        content = content,
    )
}

/** Host for the platforms which render the navigation bar on their own above the content. */
@Composable
private fun NativeNavigationHost(
    nativeNavigation: NativeNavigation,
    topLevelRoutes: List<TopLevelRoute<NavKey>>,
    selectedRoute: NavKey,
    onRouteSelected: (NavKey) -> Unit,
    content: @Composable () -> Unit,
) {
    val tabs = topLevelRoutes.map {
        NativeNavigationTab(route = it.route, title = stringResource(it.name))
    }
    val currentOnRouteSelected by rememberUpdatedState(onRouteSelected)
    LaunchedEffect(nativeNavigation, tabs, selectedRoute) {
        nativeNavigation.setTabs(
            tabs = tabs,
            selectedRoute = selectedRoute,
            onTabSelected = { currentOnRouteSelected(it) },
        )
    }
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
        CompositionLocalProvider(
            LocalNavigationBarPadding provides nativeNavigation.bottomBarHeight,
            content = content,
        )
    }
}

private fun buildTopLevelRoutes(isProcessesVisible: Boolean, isApplicationsVisible: Boolean) =
    buildList<TopLevelRoute<NavKey>> {
        add(
            TopLevelRoute(
                name = Res.string.hardware,
                route = InformationRoute,
                icon = Res.drawable.ic_cpu,
            )
        )
        if (isApplicationsVisible) {
            add(
                TopLevelRoute(
                    name = Res.string.applications,
                    route = ApplicationsRoute,
                    icon = Res.drawable.ic_android,
                )
            )
        }
        if (isProcessesVisible) {
            add(
                TopLevelRoute(
                    name = Res.string.processes,
                    route = ProcessesRoute,
                    icon = Res.drawable.ic_process,
                )
            )
        }
        add(
            TopLevelRoute(
                name = Res.string.temp,
                route = TemperaturesRoute,
                icon = Res.drawable.ic_temperature,
            )
        )
        add(
            TopLevelRoute(
                name = Res.string.settings,
                route = SettingsRoute,
                icon = Res.drawable.ic_settings,
            )
        )
    }

@Preview
@Composable
fun HostScreenPreview() {
    CpuInfoTheme { HostScreen(uiState = HostViewModel.UiState()) }
}
