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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.DenseListItem
import androidx.tv.material3.ListItemColors
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme

@Composable
fun TvListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    colors: ListItemColors =
        ListItemDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f)
        ),
    headlineContent: @Composable () -> Unit,
) {
    DenseListItem(
        selected = false,
        onClick = onClick,
        scale = ListItemDefaults.scale(focusedScale = 1.01f),
        colors = colors,
        headlineContent = headlineContent,
        modifier = modifier,
    )
}
