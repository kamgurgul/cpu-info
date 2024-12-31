@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
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
import com.kgurgul.cpuinfo.wear.features.information.WearInfoContainerScreen
import com.kgurgul.cpuinfo.wear.theme.WearAppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WearHostScreen() {
    val navController = rememberSwipeDismissableNavController()
    WearAppTheme {
        AppScaffold {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = WearHostScreen.Menu.route,
            ) {
                composable(WearHostScreen.Menu.route) {
                    MenuScreen(
                        onInformationClicked = {
                            navController.navigate(WearHostScreen.Information.route)
                        }
                    )
                }
                composable(WearHostScreen.Information.route) {
                    WearInfoContainerScreen()
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    onInformationClicked: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip,
        )
    )
    ScreenScaffold(
        scrollState = scrollState
    ) {
        ScalingLazyColumn(
            columnState = columnState,
        ) {
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
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = onInformationClicked,
                )
            }
            item {
                Chip(
                    label = stringResource(Res.string.applications),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_android),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = {},
                )
            }
            item {
                Chip(
                    label = stringResource(Res.string.temp),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_temperature),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = {},
                )
            }
            item {
                Chip(
                    label = stringResource(Res.string.settings),
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_settings),
                            contentDescription = null,
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    onClick = {},
                )
            }
        }
    }
}

sealed class WearHostScreen(val route: String) {
    data object Menu : WearHostScreen("menu")
    data object Information : WearHostScreen("information")
}