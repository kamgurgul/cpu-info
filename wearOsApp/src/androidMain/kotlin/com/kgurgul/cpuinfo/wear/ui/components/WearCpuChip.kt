package com.kgurgul.cpuinfo.wear.ui.components

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
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
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
    colors: ChipColors = ChipDefaults.primaryChipColors(),
    border: ChipBorder = ChipDefaults.chipBorder(),
) {
    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        onClick = onClick,
        colors = colors,
        border = border,
        label = {
            Text(
                text = label,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = if (secondaryLabel != null) {
            {
                Text(
                    text = secondaryLabel,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else null,
    )
}
