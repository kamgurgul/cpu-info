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
import androidx.compose.runtime.Immutable
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
 * Space the native bars take at each edge of the window. The platform places the bars, usually the
 * tab bar at the bottom, but it can move them down a side, e.g. on iPhone Duo. Sides are absolute,
 * as the bars follow the hardware and not the layout direction. [bottom] reaches the bottom edge of
 * the window, the other sides only count the bars, without the safe area screens already pad for.
 */
@Immutable
data class NativeNavigationInsets(
    val left: Dp = 0.dp,
    val top: Dp = 0.dp,
    val right: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    /**
     * Whether the platform lays the bars out down a side of the window, e.g. on iPhone Duo. The
     * back button is then shown in the system navigation bar there, see [setNavigationBarButton].
     */
    val areBarsVertical: Boolean = false,
    /**
     * Distance from the top of the window to the bottom of the system navigation bar while it runs
     * along the top, `null` otherwise. A top app bar shown in the system bar reserves this space.
     */
    val navigationBarBottom: Dp? = null,
)

/**
 * Navigation elements which are rendered by the platform above the Compose content instead of being
 * drawn by Compose (e.g. Liquid Glass tab bar and buttons on iOS).
 */
interface NativeNavigation {

    /** Where the native bars are, kept while they are hidden so the content doesn't change size. */
    val insets: NativeNavigationInsets

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

    /**
     * Adds or updates a button shown in the system navigation bar, which the platform places on its
     * own, e.g. down the side of the window on iPhone Duo. The bar shows the button of the screen
     * which most recently became the top one, [isTop] is false once the screen isn't, see
     * [isTopEntry].
     */
    fun setNavigationBarButton(
        id: String,
        type: NativeNavigationButtonType,
        contentDescription: String,
        isTop: Boolean,
        onClick: () -> Unit,
    )

    /** Removes the button added with [setButton] or [setNavigationBarButton]. */
    fun removeButton(id: String)

    /**
     * Adds or updates a title shown in the system navigation bar, picked like the button of
     * [setNavigationBarButton].
     */
    fun setNavigationBarTitle(id: String, title: String, isTop: Boolean)

    /** Removes the title added with [setNavigationBarTitle]. */
    fun removeNavigationBarTitle(id: String)
}

/**
 * Set by a top app bar shown in the system navigation bar, the navigation icon placed in it is
 * shown there as well.
 */
internal val LocalIsInSystemNavigationBar = compositionLocalOf { false }

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
