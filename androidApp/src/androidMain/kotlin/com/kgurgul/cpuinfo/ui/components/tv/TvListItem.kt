package com.kgurgul.cpuinfo.ui.components.tv

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.DenseListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme

@Composable
fun TvListItem(
    modifier: Modifier = Modifier,
    headlineContent: @Composable () -> Unit,
) {
    DenseListItem(
        selected = false,
        onClick = {},
        scale = ListItemDefaults.scale(focusedScale = 1.01f),
        colors = ListItemDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.25f),
        ),
        headlineContent = headlineContent,
        modifier = modifier,
    )
}
