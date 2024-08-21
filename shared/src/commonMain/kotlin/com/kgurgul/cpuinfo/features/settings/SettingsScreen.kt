package com.kgurgul.cpuinfo.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cancel
import com.kgurgul.cpuinfo.shared.celsius
import com.kgurgul.cpuinfo.shared.fahrenheit
import com.kgurgul.cpuinfo.shared.general
import com.kgurgul.cpuinfo.shared.kelvin
import com.kgurgul.cpuinfo.shared.pref_theme
import com.kgurgul.cpuinfo.shared.pref_theme_choose
import com.kgurgul.cpuinfo.shared.pref_theme_dark
import com.kgurgul.cpuinfo.shared.pref_theme_default
import com.kgurgul.cpuinfo.shared.pref_theme_light
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temperature_unit
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingLarge
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
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
                title = stringResource(Res.string.settings),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
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
            item(key = "__themeItem") {
                SettingsItem(
                    title = stringResource(Res.string.pref_theme),
                    subtitle = getThemeName(option = uiState.theme),
                    onClick = onThemeItemClicked,
                )
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
            .padding(start = spacingLarge)
            .focusable(),
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
                    Text(text = stringResource(Res.string.cancel))
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
                Text(text = stringResource(Res.string.pref_theme_choose))
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
                    Text(text = stringResource(Res.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun getThemeName(option: String): String {
    return when (option) {
        DarkThemeConfig.FOLLOW_SYSTEM.prefName -> stringResource(Res.string.pref_theme_default)
        DarkThemeConfig.LIGHT.prefName -> stringResource(Res.string.pref_theme_light)
        DarkThemeConfig.DARK.prefName -> stringResource(Res.string.pref_theme_dark)
        else -> throw IllegalArgumentException("Unknown theme")
    }
}

@Composable
private fun getTemperatureUnit(option: Int): String {
    return when (option) {
        TemperatureFormatter.CELSIUS -> stringResource(Res.string.celsius)
        TemperatureFormatter.FAHRENHEIT -> stringResource(Res.string.fahrenheit)
        TemperatureFormatter.KELVIN -> stringResource(Res.string.kelvin)
        else -> throw IllegalArgumentException("Unknown temperature unit")
    }
}
