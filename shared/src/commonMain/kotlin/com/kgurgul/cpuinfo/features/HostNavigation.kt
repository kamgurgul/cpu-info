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

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.savedstate.serialization.SavedStateConfiguration
import com.kgurgul.cpuinfo.features.applications.ApplicationsRoute
import com.kgurgul.cpuinfo.features.information.InformationRoute
import com.kgurgul.cpuinfo.features.processes.ProcessesRoute
import com.kgurgul.cpuinfo.features.settings.LicensesRoute
import com.kgurgul.cpuinfo.features.settings.SettingsRoute
import com.kgurgul.cpuinfo.features.temperature.TemperaturesRoute
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import com.kgurgul.cpuinfo.utils.navigation.NavigationState
import com.kgurgul.cpuinfo.utils.navigation.deepLinkMatcher
import com.kgurgul.cpuinfo.utils.navigation.findRoute
import com.kgurgul.cpuinfo.utils.navigation.rememberNavigationState
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val hostStartRoute: NavKey = InformationRoute

private val hostTopLevelRoutes: Set<NavKey> =
    setOf(InformationRoute, ApplicationsRoute, ProcessesRoute, TemperaturesRoute, SettingsRoute)

private val hostParentRoutes: Map<NavKey, NavKey> = mapOf(LicensesRoute to SettingsRoute)

private val hostSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(InformationRoute::class, InformationRoute.serializer())
            subclass(ApplicationsRoute::class, ApplicationsRoute.serializer())
            subclass(ProcessesRoute::class, ProcessesRoute.serializer())
            subclass(TemperaturesRoute::class, TemperaturesRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
            subclass(LicensesRoute::class, LicensesRoute.serializer())
        }
    }
}

private val hostDeepLinkMatchers: List<DeepLinkMatcher<NavKey, *>> =
    listOf(
        deepLinkMatcher(NavigationConst.INFORMATION, InformationRoute.serializer()),
        deepLinkMatcher(NavigationConst.APPLICATIONS, ApplicationsRoute.serializer()),
        deepLinkMatcher(NavigationConst.PROCESSES, ProcessesRoute.serializer()),
        deepLinkMatcher(NavigationConst.TEMPERATURES, TemperaturesRoute.serializer()),
        deepLinkMatcher(NavigationConst.SETTINGS, SettingsRoute.serializer()),
        deepLinkMatcher(
            NavigationConst.SETTINGS + "/" + NavigationConst.LICENSES,
            LicensesRoute.serializer(),
        ),
    )

/** Returns the route matching the deep link [uri] or null if it isn't supported. */
fun findHostDeepLinkRoute(uri: String?): NavKey? = hostDeepLinkMatchers.findRoute(uri)

/**
 * Creates the navigation state of the [HostScreen].
 *
 * @param initialRoute route (e.g. from a deep link) opened when there is no state to restore
 */
@Composable
fun rememberHostNavigationState(initialRoute: NavKey? = null): NavigationState =
    rememberNavigationState(
        startRoute = hostStartRoute,
        topLevelRoutes = hostTopLevelRoutes,
        configuration = hostSavedStateConfiguration,
        parentRoutes = hostParentRoutes,
        initialRoute = initialRoute,
    )
