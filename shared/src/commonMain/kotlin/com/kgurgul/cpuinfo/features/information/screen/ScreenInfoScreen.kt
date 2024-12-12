package com.kgurgul.cpuinfo.features.information.screen

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
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScreenInfoScreen(
    viewModel: ScreenInfoViewModel = koinViewModel(),
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
                uiState.items,
                key = { _, itemValue -> itemValue.getKey() },
            ) { index, itemValue ->
                InformationRow(
                    title = itemValue.getName(),
                    value = itemValue.getValue(),
                    isLastItem = index == uiState.items.lastIndex,
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
