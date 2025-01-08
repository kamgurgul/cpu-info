@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.kgurgul.cpuinfo.features.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearTemperatureUnitPickerScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onTemperatureUnitSelected: () -> Unit,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearTemperatureUnitPickerScreen(
        uiState = uiState,
        onTemperatureUnitSelected = {
            viewModel.setTemperatureUnit(it)
            onTemperatureUnitSelected()
        },
    )
}

@Composable
fun WearTemperatureUnitPickerScreen(
    uiState: SettingsViewModel.UiState,
    onTemperatureUnitSelected: (Int) -> Unit,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.SingleButton,
        ),
    )
    ScreenScaffold(scrollState = columnState) {

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
