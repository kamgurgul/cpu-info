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
package com.kgurgul.cpuinfo.features.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.features.settings.licenses.LicensesScreen
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.action_not_supported
import com.kgurgul.cpuinfo.shared.cancel
import com.kgurgul.cpuinfo.shared.celsius
import com.kgurgul.cpuinfo.shared.fahrenheit
import com.kgurgul.cpuinfo.shared.general
import com.kgurgul.cpuinfo.shared.ic_open_in_browser
import com.kgurgul.cpuinfo.shared.kelvin
import com.kgurgul.cpuinfo.shared.licenses
import com.kgurgul.cpuinfo.shared.pref_theme
import com.kgurgul.cpuinfo.shared.pref_theme_choose
import com.kgurgul.cpuinfo.shared.pref_theme_dark
import com.kgurgul.cpuinfo.shared.pref_theme_default
import com.kgurgul.cpuinfo.shared.pref_theme_light
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.settings_about
import com.kgurgul.cpuinfo.shared.settings_others
import com.kgurgul.cpuinfo.shared.temperature_unit
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.iconDefaultSize
import com.kgurgul.cpuinfo.ui.theme.spacingLarge
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import com.kgurgul.cpuinfo.utils.safeOpenUri
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object SettingsRoute {

    @SerialName(NavigationConst.SETTINGS) @Serializable data object List

    @SerialName(NavigationConst.SETTINGS + "_" + NavigationConst.LICENSES)
    @Serializable
    data object Licenses
}

fun NavGraphBuilder.settingsScreen(
    onLicensesClicked: () -> Unit,
    onNavigateBackClicked: () -> Unit,
) {
    navigation<SettingsRoute>(
        startDestination = SettingsRoute.List,
        deepLinks =
            listOf(
                navDeepLink<SettingsRoute>(
                    basePath = NavigationConst.BASE_URL + NavigationConst.SETTINGS
                )
            ),
    ) {
        composable<SettingsRoute.List> { SettingsScreen(onLicensesClicked = onLicensesClicked) }
        composable<SettingsRoute.Licenses>(
            deepLinks =
                listOf(
                    navDeepLink<SettingsRoute>(
                        basePath =
                            NavigationConst.BASE_URL +
                                NavigationConst.SETTINGS +
                                "/" +
                                NavigationConst.LICENSES
                    )
                )
        ) {
            LicensesScreen(onNavigateBackClicked = onNavigateBackClicked)
        }
    }
}

@Composable
fun SettingsScreen(onLicensesClicked: () -> Unit, viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onTemperatureOptionClicked = viewModel::setTemperatureUnit,
        onThemeOptionClicked = viewModel::setTheme,
        onLicensesClicked = onLicensesClicked,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
    onTemperatureOptionClicked: (Int) -> Unit,
    onThemeOptionClicked: (String) -> Unit,
    onLicensesClicked: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = { PrimaryTopAppBar(title = stringResource(Res.string.settings)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        var isTemperatureDialogVisible by remember { mutableStateOf(false) }
        var isThemeDialogVisible by remember { mutableStateOf(false) }
        SettingsList(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onThemeItemClicked = { isThemeDialogVisible = true },
            onTemperatureItemClicked = { isTemperatureDialogVisible = true },
            onLicensesClicked = onLicensesClicked,
            onAboutClicked = {
                uriHandler.safeOpenUri(NavigationConst.APP_WEBPAGE).onFailure {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.action_not_supported)
                        )
                    }
                }
            },
        )
        TemperatureUnitDialog(
            isDialogVisible = isTemperatureDialogVisible,
            onDismissRequest = { isTemperatureDialogVisible = false },
            currentSelection = uiState.temperatureUnit,
            options = uiState.temperatureDialogOptions,
            onOptionClicked = onTemperatureOptionClicked,
        )
        ThemeDialog(
            isDialogVisible = isThemeDialogVisible,
            onDismissRequest = { isThemeDialogVisible = false },
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
    onLicensesClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        LazyColumn(contentPadding = PaddingValues(spacingMedium), state = listState) {
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
            item(key = "__otherHeader") {
                Spacer(modifier = Modifier.requiredSize(spacingMedium))
                Text(
                    text = stringResource(Res.string.settings_others),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = Modifier.requiredSize(spacingMedium))
            }
            item(key = "__licensesItem") {
                SettingsItem(
                    title = stringResource(Res.string.licenses),
                    onClick = onLicensesClicked,
                )
            }
            item(key = "__aboutItem") {
                SettingsItem(
                    title = stringResource(Res.string.settings_about),
                    onClick = onAboutClicked,
                    icon = Res.drawable.ic_open_in_browser,
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    icon: DrawableResource? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .clickable(onClick = onClick)
                .padding(vertical = spacingMedium)
                .padding(start = spacingLarge),
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.requiredSize(iconDefaultSize),
            )
        }
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
            title = { Text(text = stringResource(Res.string.temperature_unit)) },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .clickable {
                                        onOptionClicked(option)
                                        onDismissRequest()
                                    }
                                    .padding(vertical = spacingSmall),
                        ) {
                            RadioButton(
                                selected = option == currentSelection,
                                onClick = null,
                                colors =
                                    RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.tertiary
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
                Button(onClick = onDismissRequest) {
                    Text(text = stringResource(Res.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun ThemeDialog(
    isDialogVisible: Boolean,
    onDismissRequest: () -> Unit,
    currentSelection: String,
    options: ImmutableList<String>,
    onOptionClicked: (String) -> Unit,
) {
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.pref_theme_choose)) },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.verticalScroll(scrollState),
                ) {
                    for (option in options) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .clickable {
                                        onOptionClicked(option)
                                        onDismissRequest()
                                    }
                                    .padding(vertical = spacingSmall),
                        ) {
                            RadioButton(
                                selected = option == currentSelection,
                                onClick = null,
                                colors =
                                    RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.tertiary
                                    ),
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
                Button(onClick = onDismissRequest) {
                    Text(text = stringResource(Res.string.cancel))
                }
            },
        )
    }
}

@Composable
fun getThemeName(option: String): String {
    return when (option) {
        DarkThemeConfig.FOLLOW_SYSTEM.prefName -> stringResource(Res.string.pref_theme_default)
        DarkThemeConfig.LIGHT.prefName -> stringResource(Res.string.pref_theme_light)
        DarkThemeConfig.DARK.prefName -> stringResource(Res.string.pref_theme_dark)
        else -> throw IllegalArgumentException("Unknown theme")
    }
}

@Composable
fun getTemperatureUnit(option: Int): String {
    return when (option) {
        TemperatureFormatter.CELSIUS -> stringResource(Res.string.celsius)
        TemperatureFormatter.FAHRENHEIT -> stringResource(Res.string.fahrenheit)
        TemperatureFormatter.KELVIN -> stringResource(Res.string.kelvin)
        else -> throw IllegalArgumentException("Unknown temperature unit")
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    CpuInfoTheme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState(),
            onTemperatureOptionClicked = {},
            onThemeOptionClicked = {},
            onLicensesClicked = {},
        )
    }
}
