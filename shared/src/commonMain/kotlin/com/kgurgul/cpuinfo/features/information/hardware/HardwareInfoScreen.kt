package com.kgurgul.cpuinfo.features.information.hardware

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HardwareInfoScreen(
    viewModel: HardwareInfoViewModel = koinViewModel(),
) {
    registerPowerPlugListener(
        onRefresh = viewModel::refreshHardwareInfo
    )
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    HardwareInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun HardwareInfoScreen(
    uiState: HardwareInfoViewModel.UiState,
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
            itemsIndexed(
                uiState.hardwareItems
            ) { index, (title, value) ->
                InformationRow(
                    title = title,
                    value = value,
                    isLastItem = index == uiState.hardwareItems.lastIndex,
                    modifier = Modifier.focusable(),
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
expect fun registerPowerPlugListener(onRefresh: () -> Unit)
