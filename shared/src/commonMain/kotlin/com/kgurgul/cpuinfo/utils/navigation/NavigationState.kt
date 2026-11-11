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
package com.kgurgul.cpuinfo.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer

/**
 * Creates a navigation state that persists config changes and process death.
 *
 * @param startRoute the start route. The user will exit the app through this route.
 * @param topLevelRoutes all top level routes. Each of them has its own back stack.
 * @param configuration configuration with all routes registered as [NavKey] subclasses. It is
 *   required because non-JVM targets can't serialize [NavKey] using reflection.
 * @param parentRoutes maps a nested route to the route which is below it on the back stack.
 * @param initialRoute route (e.g. from a deep link) opened when there is no state to restore.
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
    configuration: SavedStateConfiguration,
    parentRoutes: Map<NavKey, NavKey> = emptyMap(),
    initialRoute: NavKey? = null,
): NavigationState {
    val initialStack =
        remember(topLevelRoutes, parentRoutes) {
            initialRoute
                ?.let { buildStack(route = it, parentRoutes = parentRoutes) }
                ?.takeIf { it.first() in topLevelRoutes }
        }

    val topLevelRoute =
        rememberSerializable(
            startRoute,
            topLevelRoutes,
            serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class)),
            configuration = configuration,
        ) {
            mutableStateOf(initialStack?.first() ?: startRoute)
        }

    val backStacks = topLevelRoutes.associateWith { route ->
        key(route) {
            val elements = initialStack?.takeIf { it.first() == route } ?: listOf(route)
            rememberNavBackStack(configuration, *elements.toTypedArray())
        }
    }

    return remember(startRoute, topLevelRoutes, parentRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
            parentRoutes = parentRoutes,
        )
    }
}

/**
 * State holder for navigation state.
 *
 * @param startRoute the start route. The user will exit the app through this route.
 * @param topLevelRoute the current top level route
 * @param backStacks the back stacks for each top level route
 * @param parentRoutes maps a nested route to the route which is below it on the back stack
 */
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
    val parentRoutes: Map<NavKey, NavKey> = emptyMap(),
) {
    var topLevelRoute: NavKey by topLevelRoute

    val currentRoute: NavKey
        get() = backStacks[topLevelRoute]?.lastOrNull() ?: topLevelRoute

    val stacksInUse: List<NavKey>
        get() =
            if (topLevelRoute == startRoute || !exitThroughStartRoute) {
                listOf(topLevelRoute)
            } else {
                listOf(startRoute, topLevelRoute)
            }
}

/** Converts [NavigationState] into NavEntries. */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (route, stack) ->
        key(route) {
            val decorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                    rememberViewModelStoreNavEntryDecorator(),
                )
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider,
            )
        }
    }

    return stacksInUse.flatMap { decoratedEntries[it] ?: emptyList() }.toMutableStateList()
}

/** Builds a back stack for [route] starting from its top level route. */
internal fun buildStack(route: NavKey, parentRoutes: Map<NavKey, NavKey>): List<NavKey> {
    val stack = mutableListOf(route)
    var parent = parentRoutes[route]
    while (parent != null && parent !in stack) {
        stack.add(0, parent)
        parent = parentRoutes[parent]
    }
    return stack
}
