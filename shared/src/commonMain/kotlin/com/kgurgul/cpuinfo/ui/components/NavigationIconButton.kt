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

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.kgurgul.cpuinfo.utils.navigation.LocalIsInSystemNavigationBar
import com.kgurgul.cpuinfo.utils.navigation.LocalNativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.NativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.NativeNavigationButtonType
import com.kgurgul.cpuinfo.utils.navigation.isTopEntry

/**
 * Icon button used for navigation actions like back or close. When the platform provides native
 * navigation elements then only the space is reserved and the button is rendered by the platform.
 * Placed in a top app bar shown in the system navigation bar, or while the platform lays its bars
 * out down a side of the window, e.g. on iPhone Duo, the button is shown in the system navigation
 * bar and takes no space where it's placed.
 */
@Composable
fun NavigationIconButton(
    type: NativeNavigationButtonType,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val nativeNavigation = LocalNativeNavigation.current
    if (nativeNavigation == null) {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(
                imageVector =
                    when (type) {
                        NativeNavigationButtonType.BACK -> Icons.AutoMirrored.Filled.ArrowBack
                        NativeNavigationButtonType.CLOSE -> Icons.Default.Close
                    },
                contentDescription = contentDescription,
                tint = tint,
            )
        }
        return
    }

    val id = remember { "navigationButton${nextNativeButtonId++}" }
    val currentOnClick by rememberUpdatedState(onClick)
    // Keyed on the id only, so switching between the two kinds of buttons doesn't remove the new
    // one.
    DisposableEffect(nativeNavigation, id) { onDispose { nativeNavigation.removeButton(id) } }
    if (LocalIsInSystemNavigationBar.current || nativeNavigation.insets.areBarsVertical) {
        val isTop = isTopEntry()
        LaunchedEffect(nativeNavigation, id, type, contentDescription, isTop) {
            nativeNavigation.setNavigationBarButton(
                id = id,
                type = type,
                contentDescription = contentDescription,
                isTop = isTop,
                onClick = { currentOnClick() },
            )
        }
    } else {
        FloatingNativeButton(
            nativeNavigation = nativeNavigation,
            id = id,
            type = type,
            contentDescription = contentDescription,
            onClick = { currentOnClick() },
            modifier = modifier,
        )
    }
}

/** Reserves the space of the button, which the platform renders above it. */
@Composable
private fun FloatingNativeButton(
    nativeNavigation: NativeNavigation,
    id: String,
    type: NativeNavigationButtonType,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val density = LocalDensity.current
    val navAnimatedContentScope = LocalNavAnimatedContentScope.current
    val navTransitionAlpha =
        navAnimatedContentScope.transition.animateFloat(label = "nativeButtonAlpha") { state ->
            if (state == EnterExitState.Visible) 1f else 0f
        }
    var boundsInWindow by remember { mutableStateOf<DpRect?>(null) }
    var clipAlpha by remember { mutableFloatStateOf(1f) }
    val alpha = (navTransitionAlpha.value * clipAlpha).coerceIn(0f, 1f)
    val currentType by rememberUpdatedState(type)
    val currentContentDescription by rememberUpdatedState(contentDescription)

    LaunchedEffect(nativeNavigation, id, type, contentDescription, boundsInWindow, alpha) {
        val bounds = boundsInWindow ?: return@LaunchedEffect
        nativeNavigation.setButton(
            id = id,
            type = type,
            contentDescription = contentDescription,
            boundsInWindow = bounds,
            alpha = alpha,
            onClick = onClick,
        )
    }
    Box(
        modifier =
            modifier.size(NativeButtonSize).onGloballyPositioned { coordinates ->
                if (coordinates.isAttached) {
                    val position = coordinates.positionInWindow()
                    val size = coordinates.size
                    val clippedBounds = coordinates.boundsInWindow()
                    val newClipAlpha =
                        if (size.width > 0) {
                            (clippedBounds.width / size.width).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                    val newBounds =
                        with(density) {
                            DpRect(
                                left = position.x.toDp(),
                                top = position.y.toDp(),
                                right = (position.x + size.width).toDp(),
                                bottom = (position.y + size.height).toDp(),
                            )
                        }
                    boundsInWindow = newBounds
                    clipAlpha = newClipAlpha
                    nativeNavigation.setButton(
                        id = id,
                        type = currentType,
                        contentDescription = currentContentDescription,
                        boundsInWindow = newBounds,
                        alpha = (navTransitionAlpha.value * newClipAlpha).coerceIn(0f, 1f),
                        onClick = onClick,
                    )
                }
            }
    )
}

private val NativeButtonSize = 48.dp
private var nextNativeButtonId = 0
