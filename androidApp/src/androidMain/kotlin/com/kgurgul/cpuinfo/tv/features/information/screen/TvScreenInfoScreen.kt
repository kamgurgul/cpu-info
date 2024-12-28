package com.kgurgul.cpuinfo.tv.features.information.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoViewModel
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvScreenInfoScreen(
    viewModel: ScreenInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvScreenInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun TvScreenInfoScreen(
    uiState: ScreenInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier
            .fillMaxSize(),
    ) {
        items(
            uiState.items,
            key = { itemValue -> itemValue.getKey() },
        ) { itemValue ->
            TvListItem {
                InformationRow(
                    title = itemValue.getName(),
                    value = itemValue.getValue(),
                    isLastItem = true,
                )
            }
        }
    }
}
