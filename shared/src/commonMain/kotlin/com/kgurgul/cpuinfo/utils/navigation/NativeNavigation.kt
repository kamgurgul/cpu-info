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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey

enum class NativeNavigationButtonType {
    BACK,
    CLOSE,
}

data class NativeNavigationTab(val route: NavKey, val title: String)

/**
 * Navigation elements which are rendered by the platform above the Compose content instead of being
 * drawn by Compose (e.g. Liquid Glass tab bar and buttons on iOS).
 */
interface NativeNavigation {

    /** Height of the area at the bottom of the screen which is covered by the native tab bar. */
    val bottomBarHeight: Dp

    fun setTabs(
        tabs: List<NativeNavigationTab>,
        selectedRoute: NavKey,
        onTabSelected: (NavKey) -> Unit,
    )

    /** Adds or updates a native button placed at [boundsInWindow]. */
    fun setButton(
        id: String,
        type: NativeNavigationButtonType,
        contentDescription: String,
        boundsInWindow: DpRect,
        alpha: Float = 1f,
        onClick: () -> Unit,
    )

    fun removeButton(id: String)
}

/** Not null when the platform renders navigation elements on its own. */
val LocalNativeNavigation = staticCompositionLocalOf<NativeNavigation?> { null }

/**
 * Bottom space which is covered by a navigation bar floating above the content. Scrollable content
 * should use it as an additional content padding to be fully reachable.
 */
val LocalNavigationBarPadding = compositionLocalOf { 0.dp }

/** Content padding which takes into account [LocalNavigationBarPadding]. */
@Composable
fun contentPaddingWithNavigationBar(
    all: Dp = 0.dp,
    additionalTop: Dp = 0.dp,
    additionalBottom: Dp = 0.dp,
): PaddingValues =
    PaddingValues(
        start = all,
        top = all + additionalTop,
        end = all,
        bottom = all + LocalNavigationBarPadding.current + additionalBottom,
    )
