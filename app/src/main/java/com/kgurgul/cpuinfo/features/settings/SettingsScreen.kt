package com.kgurgul.cpuinfo.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingLarge
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onThemeItemClicked = viewModel::onThemeOptionClicked,
        onTemperatureItemClicked = viewModel::onTemperatureOptionClicked,
        onTemperatureDialogDismissRequest = viewModel::onTemperatureDialogDismissed,
        onTemperatureOptionClicked = viewModel::setTemperatureUnit,
        onThemeDialogDismissRequest = viewModel::onThemeDialogDismissed,
        onThemeOptionClicked = viewModel::setTheme,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
    onThemeItemClicked: () -> Unit,
    onTemperatureItemClicked: () -> Unit,
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
            onThemeItemClicked = onThemeItemClicked,
            onTemperatureItemClicked = onTemperatureItemClicked,
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
    onThemeItemClicked: () -> Unit,
    onTemperatureItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingMedium),
        modifier = modifier,
    ) {
        item(key = "__generalHeader") {
            Text(
                text = stringResource(id = R.string.general),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.requiredSize(spacingMedium))
        }
        item(key = "__themeItem") {
            SettingsItem(
                title = stringResource(id = R.string.pref_theme),
                subtitle = getThemeName(option = uiState.theme),
                onClick = onThemeItemClicked,
            )
        }
        item(key = "__temperatureItem") {
            SettingsItem(
                title = stringResource(id = R.string.temperature_unit),
                subtitle = getTemperatureUnit(option = uiState.temperatureUnit),
                onClick = onTemperatureItemClicked,
            )
        }
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
    onDismissRequest: () -> Unit,
    currentSelection: Int,
    options: ImmutableList<Int>?,
    onOptionClicked: (Int) -> Unit,
) {
    if (options != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.temperature_unit))
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
                                    selectedColor = MaterialTheme.colorScheme.tertiary
                                )
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
            title = {
                Text(text = stringResource(id = R.string.pref_theme_choose))
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
                                    selectedColor = MaterialTheme.colorScheme.tertiary
                                )
                            )
                            Text(
                                text = getThemeName(option = option),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
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
private fun getThemeName(option: String): String {
    return when (option) {
        DarkThemeConfig.FOLLOW_SYSTEM.prefName -> stringResource(id = R.string.pref_theme_default)
        DarkThemeConfig.LIGHT.prefName -> stringResource(id = R.string.pref_theme_light)
        DarkThemeConfig.DARK.prefName -> stringResource(id = R.string.pref_theme_dark)
        else -> throw IllegalArgumentException("Unknown theme")
    }
}

@Composable
private fun getTemperatureUnit(option: Int): String {
    return when (option) {
        TemperatureFormatter.CELSIUS -> stringResource(id = R.string.celsius)
        TemperatureFormatter.FAHRENHEIT -> stringResource(id = R.string.fahrenheit)
        TemperatureFormatter.KELVIN -> stringResource(id = R.string.kelvin)
        else -> throw IllegalArgumentException("Unknown temperature unit")
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    CpuInfoTheme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState(),
            onThemeItemClicked = {},
            onTemperatureItemClicked = {},
            onTemperatureDialogDismissRequest = {},
            onTemperatureOptionClicked = {},
            onThemeDialogDismissRequest = {},
            onThemeOptionClicked = {},
        )
    }
}
