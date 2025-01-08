@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.settings

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.features.settings.SettingsViewModel
import com.kgurgul.cpuinfo.features.settings.getTemperatureUnit
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temperature_unit
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearSettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearSettingsScreen(
        uiState = uiState,
        onTemperatureOptionClicked = viewModel::setTemperatureUnit,
    )
}

@Composable
fun WearSettingsScreen(
    uiState: SettingsViewModel.UiState,
    onTemperatureOptionClicked: (Int) -> Unit,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.SingleButton,
        ),
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
        ) {
            item(key = "__header") {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.settings),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            item(key = "__temperature") {
                WearCpuChip(
                    label = stringResource(Res.string.temperature_unit),
                    secondaryLabel = getTemperatureUnit(option = uiState.temperatureUnit),
                    onClick = { },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_temperature),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    }
                )
            }
        }
    }

    /*Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.settings),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        var isTemperatureDialogVisible by remember { mutableStateOf(false) }
        SettingsList(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onTemperatureItemClicked = { isTemperatureDialogVisible = true },
        )
        TemperatureUnitDialog(
            isDialogVisible = isTemperatureDialogVisible,
            onDismissRequest = { isTemperatureDialogVisible = false },
            currentSelection = uiState.temperatureUnit,
            options = uiState.temperatureDialogOptions,
            onOptionClicked = onTemperatureOptionClicked,
        )
    }*/
}

/*@Composable
private fun SettingsList(
    uiState: SettingsViewModel.UiState,
    onTemperatureItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(spacingMedium),
            state = listState,
            modifier = modifier,
        ) {
            item(key = "__generalHeader") {
                Text(
                    text = stringResource(Res.string.general),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = Modifier.requiredSize(spacingMedium))
            }
            item(key = "__temperatureItem") {
                SettingsItem(
                    title = stringResource(Res.string.temperature_unit),
                    subtitle = getTemperatureUnit(option = uiState.temperatureUnit),
                    onClick = onTemperatureItemClicked,
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
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = spacingMedium)
            .padding(start = spacingLarge),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TemperatureUnitDialog(
    isDialogVisible: Boolean,
    onDismissRequest: () -> Unit,
    currentSelection: Int,
    options: ImmutableList<Int>,
    onOptionClicked: (Int) -> Unit,
) {
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(Res.string.temperature_unit))
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionClicked(option)
                                    onDismissRequest()
                                }
                                .padding(vertical = spacingSmall),
                        ) {
                            RadioButton(
                                selected = option == currentSelection,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.tertiary,
                                ),
                            )
                            Text(
                                text = getTemperatureUnit(option = option),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(Res.string.cancel))
                }
            },
        )
    }
}*/
