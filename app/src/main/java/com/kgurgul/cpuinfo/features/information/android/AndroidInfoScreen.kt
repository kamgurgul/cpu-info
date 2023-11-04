package com.kgurgul.cpuinfo.features.information.android

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
fun AndroidInfoScreen(
    viewModel: AndroidInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    AndroidInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun AndroidInfoScreen(
    uiState: AndroidInfoViewModel.UiState,
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
                isLastItem = index == uiState.items.lastIndex
            )
        }
    }
}

@Preview
@Composable
fun AndroidInfoScreenPreview() {
    CpuInfoTheme {
        AndroidInfoScreen(
            uiState = AndroidInfoViewModel.UiState(
                listOf(
                    "test" to "",
                    "test" to "test",
                )
            ),
        )
    }
}
