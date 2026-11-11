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
package com.kgurgul.cpuinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.features.applications.ApplicationsRoute
import com.kgurgul.cpuinfo.features.information.InformationRoute
import com.kgurgul.cpuinfo.features.processes.ProcessesRoute
import com.kgurgul.cpuinfo.features.rememberHostNavigationState
import com.kgurgul.cpuinfo.features.settings.LicensesRoute
import com.kgurgul.cpuinfo.features.settings.SettingsRoute
import com.kgurgul.cpuinfo.features.temperature.TemperaturesRoute
import com.kgurgul.cpuinfo.ui.shouldUseDarkTheme
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import com.kgurgul.cpuinfo.utils.navigation.NavigationState
import com.kgurgul.cpuinfo.utils.navigation.Navigator
import kotlinx.browser.window
import org.koin.compose.viewmodel.koinViewModel
import org.w3c.dom.events.Event

/** Names of the routes used as the URL fragments. */
private val routeFragments: Map<NavKey, String> =
    mapOf(
        InformationRoute to NavigationConst.INFORMATION,
        ApplicationsRoute to NavigationConst.APPLICATIONS,
        ProcessesRoute to NavigationConst.PROCESSES,
        TemperaturesRoute to NavigationConst.TEMPERATURES,
        SettingsRoute to NavigationConst.SETTINGS,
        LicensesRoute to NavigationConst.SETTINGS + "_" + NavigationConst.LICENSES,
    )

@Composable
fun WebApp(hostViewModel: HostViewModel = koinViewModel()) {
    val uiState by hostViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)
    val navigationState =
        rememberHostNavigationState(initialRoute = remember { findRouteForCurrentFragment() })
    CpuInfoTheme(useDarkTheme = darkTheme) {
        HostScreen(viewModel = hostViewModel, navigationState = navigationState)
    }
    BrowserNavigationEffect(navigationState = navigationState)
}

/**
 * Binds [navigationState] with the browser: the current route is exposed as the URL fragment, every
 * route change creates a history entry and browser back/forward buttons (or manual change of the
 * fragment) restore the matching route.
 */
@Composable
private fun BrowserNavigationEffect(navigationState: NavigationState) {
    LaunchedEffect(navigationState) {
        var isInitialRoute = true
        snapshotFlow { navigationState.currentRoute }
            .collect { route ->
                val fragment = routeFragments[route]?.let { "#$it" } ?: return@collect
                when {
                    isInitialRoute -> window.history.replaceState(null, "", fragment)
                    // Fragment is already there when the route was restored from the history
                    window.location.hash != fragment -> window.history.pushState(null, "", fragment)
                }
                isInitialRoute = false
            }
    }
    DisposableEffect(navigationState) {
        val navigator = Navigator(navigationState)
        val onPopState: (Event) -> Unit = {
            findRouteForCurrentFragment()?.let { navigator.restore(it) }
        }
        window.addEventListener("popstate", onPopState)
        onDispose { window.removeEventListener("popstate", onPopState) }
    }
}

private fun findRouteForCurrentFragment(): NavKey? {
    val fragment = window.location.hash.removePrefix("#")
    return routeFragments.entries.firstOrNull { it.value == fragment }?.key
}
