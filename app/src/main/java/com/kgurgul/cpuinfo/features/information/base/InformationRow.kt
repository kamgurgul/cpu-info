package com.kgurgul.cpuinfo.features.information.base

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun LazyItemScope.InformationRow(
    title: String,
    value: String,
    isLastItem: Boolean,
) {
    val contentColor = if (value.isEmpty()) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    ItemValueRow(
        title = title,
        value = value,
        contentColor = contentColor,
    )
    if (!isLastItem) {
        CpuDivider(
            modifier = Modifier.padding(top = spacingSmall),
        )
    }
}