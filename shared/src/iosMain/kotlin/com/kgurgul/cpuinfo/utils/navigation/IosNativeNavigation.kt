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
 * Space the native bars take at each edge of the window, see [NativeNavigationInsets]. In points.
 *
 * @param areBarsVertical whether UIKit lays the bars out down a side of the window, e.g. on iPhone
 *   Duo
 * @param navigationBarBottom bottom of the system navigation bar while it runs along the top,
 *   `null` otherwise
 */
data class IosNativeBarsInsets(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val areBarsVertical: Boolean,
    val navigationBarBottom: Double?,
)

/**
 * Implemented by the iOS app. Renders navigation elements using UIKit components (Liquid Glass)
 * above the Compose content. All sizes and positions are in points.
 */
interface IosNativeNavigationRenderer {

    fun setDarkTheme(isDarkTheme: Boolean)

    fun setTabs(tabs: List<IosNativeTab>, selectedIndex: Int)

    fun setOnTabSelectedListener(listener: (Int) -> Unit)

    /** [listener] receives the space covered by the bars, whenever it changes. */
    fun setOnBarsInsetsChangedListener(listener: (IosNativeBarsInsets) -> Unit)

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

    /**
     * Shows the button in the system navigation bar, see [NativeNavigation.setNavigationBarButton].
     */
    fun setNavigationBarButton(
        id: String,
        systemImageName: String,
        accessibilityLabel: String,
        isTop: Boolean,
        onClick: () -> Unit,
    )

    fun removeButton(id: String)

    /**
     * Shows the title in the system navigation bar, see [NativeNavigation.setNavigationBarTitle].
     */
    fun setNavigationBarTitle(id: String, title: String, isTop: Boolean)

    fun removeNavigationBarTitle(id: String)
}

/** Bridge between Compose and [IosNativeNavigationRenderer]. */
class IosNativeNavigation(private val renderer: IosNativeNavigationRenderer) : NativeNavigation {

    override var insets: NativeNavigationInsets by mutableStateOf(NativeNavigationInsets())
        private set

    private var tabRoutes: List<NavKey> = emptyList()
    private var onTabSelected: (NavKey) -> Unit = {}

    init {
        renderer.setOnBarsInsetsChangedListener {
            insets =
                NativeNavigationInsets(
                    left = it.left.dp,
                    top = it.top.dp,
                    right = it.right.dp,
                    bottom = it.bottom.dp,
                    areBarsVertical = it.areBarsVertical,
                    navigationBarBottom = it.navigationBarBottom?.dp,
                )
        }
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
            systemImageName = type.systemImageName,
            accessibilityLabel = contentDescription,
            x = boundsInWindow.left.value.toDouble(),
            y = boundsInWindow.top.value.toDouble(),
            width = (boundsInWindow.right - boundsInWindow.left).value.toDouble(),
            height = (boundsInWindow.bottom - boundsInWindow.top).value.toDouble(),
            alpha = alpha.toDouble(),
            onClick = onClick,
        )
    }

    override fun setNavigationBarButton(
        id: String,
        type: NativeNavigationButtonType,
        contentDescription: String,
        isTop: Boolean,
        onClick: () -> Unit,
    ) {
        renderer.setNavigationBarButton(
            id = id,
            systemImageName = type.systemImageName,
            accessibilityLabel = contentDescription,
            isTop = isTop,
            onClick = onClick,
        )
    }

    override fun removeButton(id: String) {
        renderer.removeButton(id)
    }

    override fun setNavigationBarTitle(id: String, title: String, isTop: Boolean) {
        renderer.setNavigationBarTitle(id = id, title = title, isTop = isTop)
    }

    override fun removeNavigationBarTitle(id: String) {
        renderer.removeNavigationBarTitle(id)
    }

    private val NativeNavigationButtonType.systemImageName: String
        get() =
            when (this) {
                NativeNavigationButtonType.BACK -> "chevron.backward"
                NativeNavigationButtonType.CLOSE -> "xmark"
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
