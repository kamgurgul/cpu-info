package com.kgurgul.cpuinfo.features.information.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun ScreenInfoScreen(
    viewModel: ScreenInfoViewModel,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ScreenInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun ScreenInfoScreen(
    uiState: ScreenInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
    ) {
        itemsIndexed(
            uiState.items,
            key = { _, pair -> pair.first }
        ) { index, (title, value) ->
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
            if (index < uiState.items.lastIndex) {
                CpuDivider(
                    modifier = Modifier.padding(top = spacingSmall),
                )
            }
        }
    }
}

@Preview
@Composable
fun ScreenInfoScreenPreview() {
    CpuInfoTheme {
        ScreenInfoScreen(
            uiState = ScreenInfoViewModel.UiState(
                listOf(
                    "test" to "",
                    "test" to "test",
                )
            ),
        )
    }
}
