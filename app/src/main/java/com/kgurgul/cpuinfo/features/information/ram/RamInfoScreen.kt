package com.kgurgul.cpuinfo.features.information.ram

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils

@Composable
fun RamInfoScreen(
    viewModel: RamInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    RamInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun RamInfoScreen(
    uiState: RamInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
        uiState.ramData?.let { ramData ->
            item(key = "__total") {
                ItemValueRow(
                    title = stringResource(id = R.string.total_memory),
                    value = Utils.convertBytesToMega(ramData.total),
                    modifier = Modifier.focusable(),
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__available") {
                ItemValueRow(
                    title = stringResource(id = R.string.available_memory),
                    value = "${Utils.convertBytesToMega(ramData.available)} " +
                            "(${ramData.availablePercentage}%)",
                    modifier = Modifier.focusable(),
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__threshold") {
                ItemValueRow(
                    title = stringResource(id = R.string.threshold),
                    value = Utils.convertBytesToMega(ramData.threshold),
                    modifier = Modifier.focusable(),
                )
            }
        }
    }
}

@Preview
@Composable
fun RamInfoScreenPreview() {
    CpuInfoTheme {
        RamInfoScreen(
            uiState = RamInfoViewModel.UiState(
                ramData = RamData(
                    total = 100,
                    available = 50,
                    availablePercentage = 50,
                    threshold = 50,
                ),
            ),
        )
    }
}