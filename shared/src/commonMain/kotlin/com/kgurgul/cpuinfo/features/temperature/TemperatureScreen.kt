/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.features.temperature

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
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.kgurgul.cpuinfo.data.local.FakeUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.shared.no_temp_data
import com.kgurgul.cpuinfo.shared.temperature
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TemperaturesBaseRoute {

    @SerialName(NavigationConst.TEMPERATURES) @Serializable data object TemperaturesRoute
}

fun NavGraphBuilder.temperaturesScreen() {
    navigation<TemperaturesBaseRoute>(
        startDestination = TemperaturesBaseRoute.TemperaturesRoute,
        deepLinks =
            listOf(
                navDeepLink<TemperaturesBaseRoute>(
                    basePath = NavigationConst.BASE_URL + NavigationConst.TEMPERATURES
                )
            ),
    ) {
        composable<TemperaturesBaseRoute.TemperaturesRoute> { TemperatureScreen() }
    }
}

@Composable
fun TemperatureScreen(viewModel: TemperatureViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TemperatureScreen(uiState = uiState)
}

@Composable
fun TemperatureScreen(uiState: TemperatureViewModel.UiState) {
    Scaffold(
        topBar = { PrimaryTopAppBar(title = stringResource(Res.string.temperature)) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        if (uiState.temperatureItems.isEmpty()) {
            EmptyTemperatureList(modifier = paddingModifier)
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
    Box(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().then(modifier)) {
            items(items = temperatureItems, key = { item -> item.id }) { item ->
                TemperatureItem(item = item, temperatureFormatter = temperatureFormatter)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Composable
private fun TemperatureItem(item: TemperatureItem, temperatureFormatter: TemperatureFormatter) {
    val coroutineScope = rememberCoroutineScope()
    Row(modifier = Modifier.height(IntrinsicSize.Min).padding(spacingMedium)) {
        Icon(
            painter = painterResource(item.icon),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            modifier = Modifier.requiredSize(60.dp),
        )
        Spacer(modifier = Modifier.requiredSize(spacingSmall))
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
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

@Composable
private fun EmptyTemperatureList(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().padding(spacingMedium).then(modifier),
    ) {
        Text(
            text = stringResource(Res.string.no_temp_data),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
fun TemperatureScreenPreview() {
    CpuInfoTheme {
        TemperatureScreen(
            uiState =
                TemperatureViewModel.UiState(
                    temperatureFormatter = TemperatureFormatter(FakeUserPreferencesRepository()),
                    isLoading = false,
                    temperatureItems =
                        persistentListOf(
                            TemperatureItem(
                                id = 0,
                                icon = Res.drawable.ic_cpu_temp,
                                name = TextResource.Text("CPU"),
                                temperature = 30f,
                            ),
                            TemperatureItem(
                                id = 1,
                                icon = Res.drawable.ic_battery,
                                name = TextResource.Text("Battery"),
                                temperature = 30f,
                            ),
                        ),
                )
        )
    }
}
