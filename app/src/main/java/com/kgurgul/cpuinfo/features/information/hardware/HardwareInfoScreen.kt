package com.kgurgul.cpuinfo.features.information.hardware

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun HardwareInfoScreen(
    viewModel: HardwareInfoViewModel,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    HardwareInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun HardwareInfoScreen(
    uiState: HardwareInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
    ) {
        itemsIndexed(
            uiState.hardwareItems
        ) { index, (title, value) ->
            InformationRow(
                title = title,
                value = value,
                isLastItem = index == uiState.hardwareItems.lastIndex
            )
        }
    }
}

@Preview
@Composable
fun HardwareInfoScreenPreview() {
    CpuInfoTheme {
        HardwareInfoScreen(
            uiState = HardwareInfoViewModel.UiState(
                listOf(
                    "test" to "",
                    "test" to "test",
                )
            ),
        )
    }
}
