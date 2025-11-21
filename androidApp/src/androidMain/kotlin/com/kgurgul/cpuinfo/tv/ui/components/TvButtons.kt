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
package com.kgurgul.cpuinfo.tv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.OutlinedIconButtonDefaults
import androidx.tv.material3.WideButton
import androidx.tv.material3.WideButtonDefaults

@Composable
fun TvIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors =
            IconButtonDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.5f),
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        content = content,
    )
}

@Composable
fun TvOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier,
        colors =
            OutlinedIconButtonDefaults.colors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                focusedContentColor = MaterialTheme.colorScheme.surface,
            ),
        content = content,
    )
}

@Composable
fun TvButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors =
            ButtonDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceTint,
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        content = content,
    )
}

@Composable
fun TvWideButton(
    onClick: () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    WideButton(
        onClick = onClick,
        title = title,
        subtitle = subtitle,
        icon = icon,
        contentColor =
            WideButtonDefaults.contentColor(
                color = MaterialTheme.colorScheme.onSurface,
                focusedColor = MaterialTheme.colorScheme.onSurface,
            ),
        scale = WideButtonDefaults.scale(focusedScale = 1.05f),
        enabled = enabled,
        interactionSource = interactionSource,
        background = { TvWideButtonBackground(enabled, interactionSource) },
        modifier = modifier,
    )
}

@Composable
private fun TvWideButtonBackground(enabled: Boolean, interactionSource: MutableInteractionSource) {
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val isPressed = interactionSource.collectIsPressedAsState().value

    val backgroundColor =
        when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.01f)
            isPressed -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            isFocused -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor))
}
