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

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

/** The transitions handed to the main `NavDisplay`. */
@Immutable
class NavTransitionSpecs(
    val transitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform,
    val popTransitionSpec: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform,
    val predictivePopTransitionSpec:
        AnimatedContentTransitionScope<Scene<NavKey>>.(swipeEdge: Int) -> ContentTransform,
)

/** Cross fade between destinations, the transition the app has always used. */
val fadeNavTransitionSpecs =
    NavTransitionSpecs(
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith fadeOut() },
        predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() },
    )

/**
 * Returns the transitions the main `NavDisplay` should play for the current navigation.
 *
 * Android, Desktop and Web always cross fade. iOS cross fades only when the navigation was
 * triggered by the bottom navigation bar and otherwise uses native-feeling iOS push/pop
 * transitions.
 *
 * @param isBottomBarNavigation see [Navigator.isBottomBarNavigation].
 */
expect fun navTransitionSpecs(isBottomBarNavigation: Boolean): NavTransitionSpecs

/**
 * Whether the start route is kept underneath the active top level route ("exit through home"
 * pattern), see [NavigationState].
 *
 * Android relies on it so the system back button always leads the user out of the app through the
 * start route. iOS has no system back button, only the edge swipe gesture, and the pattern would
 * let a swipe on the root of a bottom navigation tab jump to the start route, which no native tab
 * bar does. Dropping the start route leaves a single entry in the `NavDisplay`, which disables the
 * gesture there.
 */
internal expect val exitThroughStartRoute: Boolean
