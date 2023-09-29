package com.kgurgul.cpuinfo.features.information.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import kotlin.math.roundToInt

@Composable
fun StorageScreen(
    viewModel: StorageInfoViewModel,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    StorageScreen(
        uiState = uiState
    )
}

@Composable
fun StorageScreen(
    uiState: StorageInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
    ) {
        items(
            uiState.storageItems,
            key = { it.labelRes }
        ) { storageItem ->
            val label = stringResource(id = storageItem.labelRes)
            val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
            val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
            val progress = storageItem.storageUsed.toFloat() / storageItem.storageTotal.toFloat()
            val usedPercent = (progress * 100.0).roundToInt()
            val storageDesc = "$label: $usedReadable / $totalReadable ($usedPercent%)"
            CpuProgressBar(
                label = storageDesc,
                progress = progress,
                progressHeight = 32.dp,
                prefixImageRes = storageItem.iconRes,
            )
        }
    }
}

@Preview
@Composable
fun StorageScreenPreview() {
    CpuInfoTheme {
        StorageScreen(
            uiState = StorageInfoViewModel.UiState(
                storageItems = listOf(
                    StorageItem(
                        labelRes = R.string.internal,
                        iconRes = R.drawable.root,
                        storageTotal = 100,
                        storageUsed = 50,
                    )
                )
            )
        )
    }
}