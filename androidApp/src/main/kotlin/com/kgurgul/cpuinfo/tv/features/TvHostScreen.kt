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
package com.kgurgul.cpuinfo.tv.features

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerItemDefaults
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
import com.kgurgul.cpuinfo.tv.features.applications.TvApplicationsRoute
import com.kgurgul.cpuinfo.tv.features.applications.tvApplicationsScreen
import com.kgurgul.cpuinfo.tv.features.information.TvInformationRoute
import com.kgurgul.cpuinfo.tv.features.information.tvInformationScreen
import com.kgurgul.cpuinfo.tv.features.settings.TvSettingsRoute
import com.kgurgul.cpuinfo.tv.features.settings.tvSettingsScreen
import com.kgurgul.cpuinfo.tv.features.temperature.TvTemperaturesRoute
import com.kgurgul.cpuinfo.tv.features.temperature.tvTemperaturesScreen
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import com.kgurgul.cpuinfo.utils.navigation.Navigator
import com.kgurgul.cpuinfo.utils.navigation.TopLevelRoute
import com.kgurgul.cpuinfo.utils.navigation.deepLinkMatcher
import com.kgurgul.cpuinfo.utils.navigation.findRoute
import com.kgurgul.cpuinfo.utils.navigation.rememberNavigationState
import com.kgurgul.cpuinfo.utils.navigation.toEntries
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val tvTopLevelRoutes: Set<NavKey> =
    setOf(TvInformationRoute, TvApplicationsRoute, TvTemperaturesRoute, TvSettingsRoute)

private val tvSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(TvInformationRoute::class, TvInformationRoute.serializer())
            subclass(TvApplicationsRoute::class, TvApplicationsRoute.serializer())
            subclass(TvTemperaturesRoute::class, TvTemperaturesRoute.serializer())
            subclass(TvSettingsRoute::class, TvSettingsRoute.serializer())
        }
    }
}

private val tvDeepLinkMatchers: List<DeepLinkMatcher<NavKey, *>> =
    listOf(
        deepLinkMatcher(NavigationConst.INFORMATION, TvInformationRoute.serializer()),
        deepLinkMatcher(NavigationConst.APPLICATIONS, TvApplicationsRoute.serializer()),
        deepLinkMatcher(NavigationConst.TEMPERATURES, TvTemperaturesRoute.serializer()),
        deepLinkMatcher(NavigationConst.SETTINGS, TvSettingsRoute.serializer()),
    )

@Composable
fun TvHostScreen(viewModel: HostViewModel = koinViewModel(), deepLinkUri: String? = null) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvHostScreen(uiState = uiState, deepLinkUri = deepLinkUri)
}

@Composable
fun TvHostScreen(uiState: HostViewModel.UiState, deepLinkUri: String? = null) {
    val navigationState =
        rememberNavigationState(
            startRoute = TvInformationRoute,
            topLevelRoutes = tvTopLevelRoutes,
            configuration = tvSavedStateConfiguration,
            initialRoute = remember(deepLinkUri) { tvDeepLinkMatchers.findRoute(deepLinkUri) },
        )
    val navigator = remember(navigationState) { Navigator(navigationState) }
    val entryProvider = remember {
        entryProvider<NavKey> {
            tvInformationScreen()
            tvApplicationsScreen()
            tvTemperaturesScreen()
            tvSettingsScreen()
        }
    }
    NavigationDrawer(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(spacingMedium)
                    .selectableGroup(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement =
                    Arrangement.spacedBy(spacingSmall, Alignment.CenterVertically),
            ) {
                buildTopLevelRoutes(isApplicationsVisible = uiState.isApplicationSectionVisible)
                    .forEach { topLevelRoute ->
                        NavigationDrawerItem(
                            selected = topLevelRoute.route == navigationState.topLevelRoute,
                            onClick = { navigator.navigate(topLevelRoute.route) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(topLevelRoute.icon),
                                    contentDescription = stringResource(topLevelRoute.name),
                                )
                            },
                            colors =
                                NavigationDrawerItemDefaults.colors(
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
                                    focusedContentColor = MaterialTheme.colorScheme.onBackground,
                                    selectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    selectedContainerColor =
                                        MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4f),
                                    pressedContentColor = MaterialTheme.colorScheme.onBackground,
                                    pressedContainerColor =
                                        MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4f),
                                ),
                        ) {
                            Text(
                                text = stringResource(topLevelRoute.name),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
            }
        },
    ) {
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            popTransitionSpec = { fadeIn() togetherWith fadeOut() },
            predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() },
        )
    }
}

private fun buildTopLevelRoutes(isApplicationsVisible: Boolean) =
    buildList<TopLevelRoute<NavKey>> {
        add(
            TopLevelRoute(
                name = Res.string.hardware,
                route = TvInformationRoute,
                icon = Res.drawable.ic_cpu,
            )
        )
        if (isApplicationsVisible) {
            add(
                TopLevelRoute(
                    name = Res.string.applications,
                    route = TvApplicationsRoute,
                    icon = Res.drawable.ic_android,
                )
            )
        }
        add(
            TopLevelRoute(
                name = Res.string.temp,
                route = TvTemperaturesRoute,
                icon = Res.drawable.ic_temperature,
            )
        )
        add(
            TopLevelRoute(
                name = Res.string.settings,
                route = TvSettingsRoute,
                icon = Res.drawable.ic_settings,
            )
        )
    }
