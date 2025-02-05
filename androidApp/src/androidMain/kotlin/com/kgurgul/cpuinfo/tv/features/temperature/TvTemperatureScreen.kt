package com.kgurgul.cpuinfo.tv.features.temperature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.features.temperature.TemperatureViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.no_temp_data
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TvTemperaturesRoute

fun NavGraphBuilder.tvTemperaturesScreen() {
    composable<TvTemperaturesRoute>(
        deepLinks = listOf(
            navDeepLink<TvTemperaturesRoute>(
                basePath = NavigationConst.BASE_URL + NavigationConst.TEMPERATURES
            )
        )
    ) {
        TvTemperatureScreen()
    }
}

@Composable
fun TvTemperatureScreen(
    viewModel: TemperatureViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvTemperatureScreen(
        uiState = uiState,
    )
}

@Composable
fun TvTemperatureScreen(
    uiState: TemperatureViewModel.UiState,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        if (uiState.temperatureItems.isEmpty()) {
            TvEmptyTemperatureList(
                modifier = paddingModifier,
            )
        } else {
            TvTemperatureList(
                temperatureItems = uiState.temperatureItems,
                temperatureFormatter = uiState.temperatureFormatter,
                modifier = paddingModifier,
            )
        }
    }
}

@Composable
private fun TvTemperatureList(
    temperatureItems: ImmutableList<TemperatureItem>,
    temperatureFormatter: TemperatureFormatter,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacingMedium)
            .then(modifier),
    ) {
        items(
            items = temperatureItems,
            key = { item -> item.id },
        ) { item ->
            TvTemperatureItem(
                item = item,
                temperatureFormatter = temperatureFormatter,
            )
        }
    }
}

@Composable
private fun TvTemperatureItem(
    item: TemperatureItem,
    temperatureFormatter: TemperatureFormatter,
) {
    val coroutineScope = rememberCoroutineScope()
    TvListItem {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
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
                    text = item.name.asString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                if (!item.temperature.isNaN()) {
                    var formattedTemp by remember { mutableStateOf("") }
                    LaunchedEffect(item.temperature) {
                        coroutineScope.launch {
                            formattedTemp = temperatureFormatter.format(item.temperature)
                        }
                    }
                    Text(
                        text = formattedTemp,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun TvEmptyTemperatureList(
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
