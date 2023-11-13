package com.kgurgul.cpuinfo.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.utils.ThemeHelper
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onTemperatureDialogDismissRequest = viewModel::onTemperatureDialogDismissed,
        onTemperatureOptionClicked = viewModel::setTemperatureUnit,
        onThemeDialogDismissRequest = viewModel::onThemeDialogDismissed,
        onThemeOptionClicked = viewModel::setTheme,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
    onTemperatureDialogDismissRequest: () -> Unit,
    onTemperatureOptionClicked: (Int) -> Unit,
    onThemeDialogDismissRequest: () -> Unit,
    onThemeOptionClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(id = R.string.settings),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        SettingsList(
            uiState = uiState,
            modifier = paddingModifier,
        )
        TemperatureUnitDialog(
            onDismissRequest = onTemperatureDialogDismissRequest,
            currentSelection = uiState.temperatureUnit,
            options = uiState.temperatureDialogOptions,
            onOptionClicked = onTemperatureOptionClicked,
        )
        ThemeDialog(
            onDismissRequest = onThemeDialogDismissRequest,
            currentSelection = uiState.theme,
            options = uiState.themeDialogOptions,
            onOptionClicked = onThemeOptionClicked,
        )
    }
}

@Composable
private fun SettingsList(
    uiState: SettingsViewModel.UiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        uiState.temperatureUnit
    }
}

@Composable
private fun TemperatureUnitDialog(
    onDismissRequest: () -> Unit,
    currentSelection: Int,
    options: ImmutableList<Int>?,
    onOptionClicked: (Int) -> Unit,
) {
    if (options != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            tonalElevation = 0.dp,
            title = {
                Text(text = stringResource(id = R.string.temperature_unit))
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingMedium),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionClicked(option)
                                    onDismissRequest()
                                }
                        ) {
                            RadioButton(
                                selected = option == currentSelection,
                                onClick = null,
                            )
                            val text = when (option) {
                                TemperatureFormatter.CELSIUS ->
                                    stringResource(id = R.string.celsius)

                                TemperatureFormatter.FAHRENHEIT ->
                                    stringResource(id = R.string.fahrenheit)

                                TemperatureFormatter.KELVIN ->
                                    stringResource(id = R.string.kelvin)

                                else -> throw IllegalArgumentException("Unknown temperature unit")
                            }
                            Text(text = text)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun ThemeDialog(
    onDismissRequest: () -> Unit,
    currentSelection: String,
    options: ImmutableList<String>?,
    onOptionClicked: (String) -> Unit,
) {
    if (options != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            tonalElevation = 0.dp,
            title = {
                Text(text = stringResource(id = R.string.pref_theme_choose))
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingMedium),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionClicked(option)
                                    onDismissRequest()
                                }
                        ) {
                            RadioButton(
                                selected = option == currentSelection,
                                onClick = null,
                            )
                            val text = when (option) {
                                ThemeHelper.DEFAULT_MODE ->
                                    stringResource(id = R.string.pref_theme_default)

                                ThemeHelper.LIGHT_MODE ->
                                    stringResource(id = R.string.pref_theme_light)

                                ThemeHelper.DARK_MODE ->
                                    stringResource(id = R.string.pref_theme_dark)

                                else -> throw IllegalArgumentException("Unknown theme")
                            }
                            Text(text = text)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    CpuInfoTheme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState(),
            onTemperatureDialogDismissRequest = {},
            onTemperatureOptionClicked = {},
            onThemeDialogDismissRequest = {},
            onThemeOptionClicked = {},
        )
    }
}
