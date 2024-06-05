package com.kgurgul.cpuinfo.ui.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CpuDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
        modifier = modifier,
    )
}