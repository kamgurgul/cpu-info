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
package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.navigation.LocalIsInSystemNavigationBar
import com.kgurgul.cpuinfo.utils.navigation.LocalNativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.NativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.isTopEntry

/**
 * Top app bar with a surface background. When the platform provides a system navigation bar (iOS)
 * and the bar has no [actions], the title and the navigation icon (see [NavigationIconButton]) are
 * shown in the system bar, so they look and are placed like in native apps, e.g. next to the status
 * bar on the unfolded iPhone Duo. [windowInsets] and [scrollBehavior] don't apply to it then.
 */
@Composable
fun SurfaceTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val nativeNavigation = LocalNativeNavigation.current
    val navigationBarBottom = nativeNavigation?.insets?.navigationBarBottom
    if (nativeNavigation != null && navigationBarBottom != null && actions == null) {
        SystemNavigationTopAppBar(
            nativeNavigation = nativeNavigation,
            navigationBarBottom = navigationBarBottom,
            title = title,
            navigationIcon = navigationIcon,
            modifier = modifier,
        )
        return
    }
    CpuTopAppBar(
        title =
            if (title != null) {
                {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            } else null,
        navigationIcon = navigationIcon,
        actions = actions ?: {},
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

/**
 * Shows the title in the system navigation bar, which the platform draws above the Compose content,
 * and takes the place of the bar. The bar shows the content of the top screen, so it changes once a
 * back gesture completes, not when it starts.
 */
@Composable
private fun SystemNavigationTopAppBar(
    nativeNavigation: NativeNavigation,
    navigationBarBottom: Dp,
    title: String?,
    navigationIcon: (@Composable () -> Unit)?,
    modifier: Modifier,
) {
    val id = remember { "navigationBarTitle${nextNavigationBarTitleId++}" }
    val isTop = isTopEntry()
    DisposableEffect(nativeNavigation, id) {
        onDispose { nativeNavigation.removeNavigationBarTitle(id) }
    }
    LaunchedEffect(nativeNavigation, id, title, isTop) {
        nativeNavigation.setNavigationBarTitle(id = id, title = title.orEmpty(), isTop = isTop)
    }
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(navigationBarBottom)
                .background(MaterialTheme.colorScheme.surface)
                .testTag(TEST_TAG_CPU_TOP_APP_BAR)
    ) {
        CompositionLocalProvider(LocalIsInSystemNavigationBar provides true) {
            navigationIcon?.invoke()
        }
    }
}

@Composable
private fun CpuTopAppBar(
    colors: TopAppBarColors,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { title?.invoke() },
        navigationIcon = { navigationIcon?.invoke() },
        actions = actions,
        colors = colors,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier.testTag(TEST_TAG_CPU_TOP_APP_BAR),
    )
}

const val TEST_TAG_CPU_TOP_APP_BAR = "CpuTopAppBar"

private var nextNavigationBarTitleId = 0

@Preview
@Composable
fun TopAppBarPreview() {
    CpuInfoTheme {
        Column {
            SurfaceTopAppBar(
                title = "Title",
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }
                },
            )
        }
    }
}
