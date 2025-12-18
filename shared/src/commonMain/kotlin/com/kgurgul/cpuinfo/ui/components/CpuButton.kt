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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_thrash
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    iconResource: DrawableResource? = null,
    iconContentDescription: String? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding =
            if (iconResource != null) {
                ButtonDefaults.ButtonWithIconContentPadding
            } else {
                ButtonDefaults.ContentPadding
            },
        colors = colors,
        elevation = elevation,
        modifier = modifier,
    ) {
        if (iconResource != null) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = iconContentDescription,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text)
    }
}

@Composable
fun FilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        modifier = modifier,
        content = content,
    )
}

@Preview
@Composable
fun FilledButtonPreview() {
    Row {
        CpuInfoTheme {
            Column {
                FilledButton(
                    text = "Thrash",
                    onClick = {},
                    iconResource = Res.drawable.ic_thrash,
                    iconContentDescription = "Thrash",
                )
                FilledButton(
                    text = "Thrash",
                    onClick = {},
                    iconResource = Res.drawable.ic_thrash,
                    iconContentDescription = "Thrash",
                    enabled = false,
                )
            }
        }
        CpuInfoTheme(useDarkTheme = true) {
            Column {
                FilledButton(
                    text = "Thrash",
                    onClick = {},
                    iconResource = Res.drawable.ic_thrash,
                    iconContentDescription = "Thrash",
                )
                FilledButton(
                    text = "Thrash",
                    onClick = {},
                    iconResource = Res.drawable.ic_thrash,
                    iconContentDescription = "Thrash",
                    enabled = false,
                )
            }
        }
    }
}
