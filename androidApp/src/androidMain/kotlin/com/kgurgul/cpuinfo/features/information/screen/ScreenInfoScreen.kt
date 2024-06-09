package com.kgurgul.cpuinfo.features.information.screen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun ScreenInfoScreen(
    viewModel: ScreenInfoViewModel = hiltViewModel(),
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
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            uiState.items,
            key = { _, pair -> pair.first }
        ) { index, (title, value) ->
            InformationRow(
                title = title,
                value = value,
                isLastItem = index == uiState.items.lastIndex,
                modifier = Modifier.focusable(),
            )
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
