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

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

/** Handles navigation events (forward and back) by updating the navigation state. */
@Stable
class Navigator(val state: NavigationState) {

    /**
     * Whether the last navigation was triggered by the bottom navigation bar. Decides which
     * transition the main `NavDisplay` plays, see [navTransitionSpecs].
     */
    var isBottomBarNavigation by mutableStateOf(false)
        private set

    fun navigate(route: NavKey, fromBottomBar: Boolean = false) {
        isBottomBarNavigation = fromBottomBar
        if (route in state.backStacks.keys) {
            if (route == state.topLevelRoute) {
                val stack = state.backStacks[route] ?: return
                while (stack.size > 1) {
                    stack.removeLast()
                }
            } else {
                // This is a top level route, just switch to it.
                state.topLevelRoute = route
            }
        } else {
            val stack = state.backStacks[state.topLevelRoute] ?: return
            if (stack.lastOrNull() != route) {
                stack.add(route)
            }
        }
    }

    fun goBack() {
        isBottomBarNavigation = false
        val currentStack =
            state.backStacks[state.topLevelRoute]
                ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    /**
     * Makes [route] the current one no matter what is currently displayed. Top level route and its
     * back stack are rebuilt, so it can be used for restoring a destination from e.g. browser
     * history. Returns false if [route] doesn't belong to any top level route.
     */
    fun restore(route: NavKey): Boolean {
        isBottomBarNavigation = false
        val stack = buildStack(route = route, parentRoutes = state.parentRoutes)
        val backStack = state.backStacks[stack.first()] ?: return false
        // Both stacks start with the same top level route, so the back stack is never empty.
        val commonSize = backStack.zip(stack).takeWhile { (old, new) -> old == new }.size
        while (backStack.size > commonSize) {
            backStack.removeLast()
        }
        backStack.addAll(stack.drop(commonSize))
        state.topLevelRoute = stack.first()
        return true
    }
}
