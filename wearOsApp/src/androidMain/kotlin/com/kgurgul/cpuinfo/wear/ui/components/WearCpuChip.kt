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
package com.kgurgul.cpuinfo.wear.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipBorder
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text

@Composable
fun WearCpuChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    border: ChipBorder = ChipDefaults.chipBorder(),
    content: @Composable RowScope.() -> Unit,
) {
    Chip(
        modifier = Modifier.fillMaxWidth().then(modifier),
        onClick = onClick,
        colors = colors,
        border = border,
        content = content,
    )
}

@Composable
fun WearCpuChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    secondaryLabel: String? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    border: ChipBorder = ChipDefaults.chipBorder(),
    labelMaxLines: Int = 3,
    secondaryLabelMaxLines: Int = 3,
) {
    Chip(
        modifier = Modifier.fillMaxWidth().then(modifier),
        onClick = onClick,
        colors = colors,
        border = border,
        label = { Text(text = label, maxLines = labelMaxLines, overflow = TextOverflow.Ellipsis) },
        secondaryLabel =
            if (!secondaryLabel.isNullOrEmpty()) {
                {
                    Text(
                        text = secondaryLabel,
                        maxLines = secondaryLabelMaxLines,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else null,
        icon = icon,
    )
}
