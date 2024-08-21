package com.kgurgul.cpuinfo.features.temperature

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.no_temp_data
import com.kgurgul.cpuinfo.shared.temperature
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TemperatureScreen(
    viewModel: TemperatureViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TemperatureScreen(
        uiState = uiState,
    )
}

@Composable
fun TemperatureScreen(
    uiState: TemperatureViewModel.UiState,
) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.temperature),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        if (uiState.temperatureItems.isEmpty()) {
            EmptyTemperatureList(
                modifier = paddingModifier,
            )
        } else {
            TemperatureList(
                temperatureItems = uiState.temperatureItems,
                temperatureFormatter = uiState.temperatureFormatter,
                modifier = paddingModifier,
            )
        }
    }
}

@Composable
private fun TemperatureList(
    temperatureItems: ImmutableList<TemperatureItem>,
    temperatureFormatter: TemperatureFormatter,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
        ) {
            items(
                items = temperatureItems,
                key = { item -> item.id },
            ) { item ->
                TemperatureItem(
                    item = item,
                    temperatureFormatter = temperatureFormatter,
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
private fun TemperatureItem(
    item: TemperatureItem,
    temperatureFormatter: TemperatureFormatter
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(spacingMedium)
            .focusable(),
    ) {
        Icon(
            painter = painterResource(item.icon),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            modifier = Modifier.requiredSize(60.dp),
        )
        Spacer(modifier = Modifier.requiredSize(spacingSmall))
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.requiredSize(spacingSmall))
            if (!item.temperature.isNaN()) {
                Text(
                    text = temperatureFormatter.format(item.temperature),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

@Composable
private fun EmptyTemperatureList(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(spacingMedium)
            .then(modifier),
    ) {
        Text(
            text = stringResource(Res.string.no_temp_data),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
