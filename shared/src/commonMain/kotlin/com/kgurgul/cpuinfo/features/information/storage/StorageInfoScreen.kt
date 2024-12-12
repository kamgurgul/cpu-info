package com.kgurgul.cpuinfo.features.information.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StorageInfoScreen(
    viewModel: StorageInfoViewModel = koinViewModel(),
) {
    registerStorageMountingListener(viewModel::onRefreshStorage)
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    StorageInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun StorageInfoScreen(
    uiState: StorageInfoViewModel.UiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(spacingSmall),
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                uiState.storageItems,
                key = { it.id },
            ) { storageItem ->
                val label = buildString {
                    val notFormattedLabel = storageItem.label.asString().trim()
                    if (notFormattedLabel.isNotEmpty()) {
                        append(notFormattedLabel)
                        append(": ")
                    }
                }
                val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
                val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
                val progress = if (storageItem.storageTotal != 0L)
                    storageItem.storageUsed.toFloat() / storageItem.storageTotal.toFloat()
                else 0f
                val usedPercent = (progress * 100.0).roundToInt()
                val storageDesc = "$label$usedReadable / $totalReadable ($usedPercent%)"
                val minMaxValues = Utils.humanReadableByteCount(0) to
                    Utils.humanReadableByteCount(storageItem.storageTotal)
                CpuProgressBar(
                    label = storageDesc,
                    progress = progress,
                    progressHeight = 32.dp,
                    prefixImageRes = storageItem.iconDrawable,
                    minMaxValues = minMaxValues,
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Composable
expect fun registerStorageMountingListener(onRefresh: () -> Unit)
