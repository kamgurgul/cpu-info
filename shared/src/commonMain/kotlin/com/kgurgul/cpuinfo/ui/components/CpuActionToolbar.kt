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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.search
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.stringResource

/**
 * Top content padding to apply to scrollable content so its initial items are rendered below the
 * floating [CpuActionToolbar] while still scrolling underneath it.
 */
val CpuActionToolbarContentPadding = 72.dp

private val ToolbarItemHeight = 48.dp

/** Floating action toolbar aligned to the top-right corner. */
@Composable
fun CpuActionToolbar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier =
            modifier.fillMaxWidth().padding(horizontal = spacingMedium, vertical = spacingSmall),
    ) {
        HorizontalFloatingToolbar(
            expanded = true,
            colors =
                FloatingToolbarDefaults.standardFloatingToolbarColors(
                    toolbarContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    toolbarContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            expandedShadowElevation = 3.dp,
            content = content,
        )
    }
}

@Composable
fun RowScope.CpuToolbarSearchItem(
    showSearch: Boolean,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onSearchClosed: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(showSearch) {
        if (!showSearch) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }
    val expandProgress by
        animateFloatAsState(
            targetValue = if (showSearch) 1f else 0f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "searchExpandProgress",
        )
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier =
            Modifier.weight(1f, fill = false).layout { measurable, constraints ->
                val itemSizePx = ToolbarItemHeight.roundToPx()
                val minWidth = itemSizePx.coerceAtMost(constraints.maxWidth)
                val maxWidth = constraints.maxWidth.coerceAtLeast(minWidth)
                val currentWidth = lerp(minWidth, maxWidth, expandProgress)
                val fixedHeight = itemSizePx.coerceAtMost(constraints.maxHeight)
                val placeable = measurable.measure(Constraints.fixed(currentWidth, fixedHeight))
                layout(currentWidth, fixedHeight) { placeable.placeRelative(0, 0) }
            },
    ) {
        IconButton(
            onClick = onSearchClicked,
            enabled = !showSearch,
            modifier =
                Modifier.graphicsLayer { alpha = (1f - expandProgress * 3f).coerceIn(0f, 1f) }
                    .then(if (showSearch) Modifier.clearAndSetSemantics {} else Modifier),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(Res.string.search),
            )
        }
        if (showSearch || expandProgress > 0f) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                CpuSearchTextField(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onSearchClosed = onSearchClosed,
                    trailingIconAlpha = ((expandProgress - 0.35f) / 0.65f).coerceIn(0f, 1f),
                    modifier =
                        Modifier.graphicsLayer { alpha = (expandProgress * 2.5f).coerceIn(0f, 1f) },
                )
            }
        }
    }
}
