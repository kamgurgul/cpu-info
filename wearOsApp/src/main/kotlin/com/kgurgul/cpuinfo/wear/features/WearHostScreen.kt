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
@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.ic_android
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_temperature
import com.kgurgul.cpuinfo.shared.information
import com.kgurgul.cpuinfo.shared.menu
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.shared.temp
import com.kgurgul.cpuinfo.wear.features.applications.WearApplicationsScreen
import com.kgurgul.cpuinfo.wear.features.information.WearInfoContainerScreen
import com.kgurgul.cpuinfo.wear.features.settings.WearSettingsScreen
import com.kgurgul.cpuinfo.wear.features.settings.WearTemperatureUnitPickerScreen
import com.kgurgul.cpuinfo.wear.features.temperature.WearTemperatureScreen
import com.kgurgul.cpuinfo.wear.navigation.rememberHoistedSwipeDismissableSceneStrategy
import com.kgurgul.cpuinfo.wear.theme.WearAppTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
sealed interface WearRoute : NavKey {
    @Serializable data object Menu : WearRoute

    @Serializable data object Information : WearRoute

    @Serializable data object Applications : WearRoute

    @Serializable data object Temperature : WearRoute

    @Serializable data object Settings : WearRoute

    @Serializable data object TemperatureUnitPicker : WearRoute
}

private val wearSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(WearRoute.Menu::class, WearRoute.Menu.serializer())
            subclass(WearRoute.Information::class, WearRoute.Information.serializer())
            subclass(WearRoute.Applications::class, WearRoute.Applications.serializer())
            subclass(WearRoute.Temperature::class, WearRoute.Temperature.serializer())
            subclass(WearRoute.Settings::class, WearRoute.Settings.serializer())
            subclass(
                WearRoute.TemperatureUnitPicker::class,
                WearRoute.TemperatureUnitPicker.serializer(),
            )
        }
    }
}

/** Content key of the settings entry. Its ViewModels are shared with the nested settings entries */
private const val SETTINGS_CONTENT_KEY = "settings"

@Composable
fun WearHostScreen(viewModel: HostViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(wearSavedStateConfiguration, WearRoute.Menu)
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()
    val viewModelStoreProvider = rememberViewModelStoreProvider()
    WearAppTheme {
        AppScaffold(modifier = Modifier.background(MaterialTheme.colors.background)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators =
                    listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(viewModelStoreProvider),
                    ),
                sceneStrategies =
                    listOf(rememberHoistedSwipeDismissableSceneStrategy(swipeToDismissBoxState)),
                entryProvider =
                    entryProvider {
                        entry<WearRoute.Menu> {
                            MenuScreen(
                                uiState = uiState,
                                onInformationClicked = { backStack.add(WearRoute.Information) },
                                onApplicationsClicked = { backStack.add(WearRoute.Applications) },
                                onTemperatureClicked = { backStack.add(WearRoute.Temperature) },
                                onSettingsClicked = { backStack.add(WearRoute.Settings) },
                            )
                        }
                        entry<WearRoute.Information> { WearInfoContainerScreen() }
                        entry<WearRoute.Applications> {
                            WearApplicationsScreen(swipeToDismissBoxState = swipeToDismissBoxState)
                        }
                        entry<WearRoute.Temperature> { WearTemperatureScreen() }
                        entry<WearRoute.Settings>(clazzContentKey = { SETTINGS_CONTENT_KEY }) {
                            WearSettingsScreen(
                                onTemperatureUnitClicked = {
                                    backStack.add(WearRoute.TemperatureUnitPicker)
                                }
                            )
                        }
                        entry<WearRoute.TemperatureUnitPicker> {
                            // Share the ViewModel with the settings entry. It is cleared when the
                            // settings entry is removed from the back stack.
                            val settingsViewModelStoreOwner =
                                rememberViewModelStoreOwner(
                                    key = SETTINGS_CONTENT_KEY,
                                    provider = viewModelStoreProvider,
                                )
                            WearTemperatureUnitPickerScreen(
                                viewModel =
                                    koinViewModel(
                                        viewModelStoreOwner = settingsViewModelStoreOwner
                                    ),
                                onTemperatureUnitSelected = { backStack.removeLastOrNull() },
                            )
                        }
                    },
            )
        }
    }
}

@Composable
fun MenuScreen(
    uiState: HostViewModel.UiState,
    onInformationClicked: () -> Unit,
    onApplicationsClicked: () -> Unit,
    onTemperatureClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    val columnState =
        rememberResponsiveColumnState(
            contentPadding =
                ScalingLazyColumnDefaults.padding(first = ItemType.Text, last = ItemType.Chip)
        )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.menu),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            item {
                Chip(
                    label = stringResource(Res.string.information),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cpu),
                            contentDescription = null,
                            modifier =
                                Modifier.size(ChipDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onInformationClicked,
                )
            }
            if (uiState.isApplicationSectionVisible) {
                item {
                    Chip(
                        label = stringResource(Res.string.applications),
                        icon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_android),
                                contentDescription = null,
                                modifier =
                                    Modifier.size(ChipDefaults.IconSize)
                                        .wrapContentSize(align = Alignment.Center),
                            )
                        },
                        onClick = onApplicationsClicked,
                    )
                }
            }
            item {
                Chip(
                    label = stringResource(Res.string.temp),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_temperature),
                            contentDescription = null,
                            modifier =
                                Modifier.size(ChipDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onTemperatureClicked,
                )
            }
            item {
                Chip(
                    label = stringResource(Res.string.settings),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_settings),
                            contentDescription = null,
                            modifier =
                                Modifier.size(ChipDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onSettingsClicked,
                )
            }
        }
    }
}
