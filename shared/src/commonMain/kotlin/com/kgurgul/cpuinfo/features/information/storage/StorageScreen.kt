package com.kgurgul.cpuinfo.features.information.storage

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun StorageScreen(
    viewModel: StorageInfoViewModel = koinViewModel(),
) {
    registerStorageMountingListener(viewModel::onRefreshStorage)
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
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            uiState.storageItems,
            key = { it.id }
        ) { storageItem ->
            val label = stringResource(storageItem.label)
            val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
            val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
            val progress = storageItem.storageUsed.toFloat() / storageItem.storageTotal.toFloat()
            val usedPercent = (progress * 100.0).roundToInt()
            val storageDesc = "$label: $usedReadable / $totalReadable ($usedPercent%)"
            CpuProgressBar(
                label = storageDesc,
                progress = progress,
                progressHeight = 32.dp,
                prefixImageRes = storageItem.iconDrawable,
                modifier = Modifier.focusable(),
            )
        }
    }
}

@Composable
expect fun registerStorageMountingListener(onRefresh: () -> Unit)
