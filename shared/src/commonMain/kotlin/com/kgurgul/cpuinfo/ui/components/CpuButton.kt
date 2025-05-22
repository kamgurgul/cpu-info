package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    iconResource: DrawableResource? = null,
    iconContentDescription: String? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = if (iconResource != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier,
    ) {
        if (iconResource != null) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = iconContentDescription,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text)
    }
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
        CpuInfoTheme(
            useDarkTheme = true
        ) {
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
