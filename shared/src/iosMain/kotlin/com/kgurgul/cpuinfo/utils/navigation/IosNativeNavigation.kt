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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.kgurgul.cpuinfo.features.applications.ApplicationsRoute
import com.kgurgul.cpuinfo.features.information.InformationRoute
import com.kgurgul.cpuinfo.features.processes.ProcessesRoute
import com.kgurgul.cpuinfo.features.settings.SettingsRoute
import com.kgurgul.cpuinfo.features.temperature.TemperaturesRoute

data class IosNativeTab(val title: String, val systemImageName: String)

/**
 * Implemented by the iOS app. Renders navigation elements using UIKit components (Liquid Glass)
 * above the Compose content. All sizes and positions are in points.
 */
interface IosNativeNavigationRenderer {

    fun setDarkTheme(isDarkTheme: Boolean)

    fun setTabs(tabs: List<IosNativeTab>, selectedIndex: Int)

    fun setOnTabSelectedListener(listener: (Int) -> Unit)

    /** [listener] receives the height of the bottom area covered by the tab bar. */
    fun setOnTabBarHeightChangedListener(listener: (Double) -> Unit)

    fun setButton(
        id: String,
        systemImageName: String,
        accessibilityLabel: String,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        alpha: Double,
        onClick: () -> Unit,
    )

    fun removeButton(id: String)
}

/** Bridge between Compose and [IosNativeNavigationRenderer]. */
class IosNativeNavigation(private val renderer: IosNativeNavigationRenderer) : NativeNavigation {

    override var bottomBarHeight: Dp by mutableStateOf(0.dp)
        private set

    private var tabRoutes: List<NavKey> = emptyList()
    private var onTabSelected: (NavKey) -> Unit = {}

    init {
        renderer.setOnTabBarHeightChangedListener { bottomBarHeight = it.dp }
        renderer.setOnTabSelectedListener { index ->
            tabRoutes.getOrNull(index)?.let { onTabSelected(it) }
        }
    }

    fun setDarkTheme(isDarkTheme: Boolean) {
        renderer.setDarkTheme(isDarkTheme)
    }

    override fun setTabs(
        tabs: List<NativeNavigationTab>,
        selectedRoute: NavKey,
        onTabSelected: (NavKey) -> Unit,
    ) {
        this.tabRoutes = tabs.map { it.route }
        this.onTabSelected = onTabSelected
        renderer.setTabs(
            tabs =
                tabs.map {
                    IosNativeTab(title = it.title, systemImageName = it.route.systemImageName)
                },
            selectedIndex = tabRoutes.indexOf(selectedRoute),
        )
    }

    override fun setButton(
        id: String,
        type: NativeNavigationButtonType,
        contentDescription: String,
        boundsInWindow: DpRect,
        alpha: Float,
        onClick: () -> Unit,
    ) {
        renderer.setButton(
            id = id,
            systemImageName =
                when (type) {
                    NativeNavigationButtonType.BACK -> "chevron.backward"
                    NativeNavigationButtonType.CLOSE -> "xmark"
                },
            accessibilityLabel = contentDescription,
            x = boundsInWindow.left.value.toDouble(),
            y = boundsInWindow.top.value.toDouble(),
            width = (boundsInWindow.right - boundsInWindow.left).value.toDouble(),
            height = (boundsInWindow.bottom - boundsInWindow.top).value.toDouble(),
            alpha = alpha.toDouble(),
            onClick = onClick,
        )
    }

    override fun removeButton(id: String) {
        renderer.removeButton(id)
    }

    /** SF Symbols used for the tabs */
    private val NavKey.systemImageName: String
        get() =
            when (this) {
                InformationRoute -> "cpu"
                ApplicationsRoute -> "square.grid.2x2"
                ProcessesRoute -> "list.bullet.rectangle"
                TemperaturesRoute -> "thermometer.medium"
                SettingsRoute -> "gearshape"
                else -> "circle"
            }
}
