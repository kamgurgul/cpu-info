@file:OptIn(ExperimentalHorologistApi::class, ExperimentalWearMaterialApi::class)

package com.kgurgul.cpuinfo.wear.features.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.SwipeToDismissBoxState
import androidx.wear.compose.foundation.edgeSwipeToDismiss
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.RevealValue
import androidx.wear.compose.material.SwipeToRevealChip
import androidx.wear.compose.material.SwipeToRevealDefaults
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.SwipeToRevealSecondaryAction
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleButton
import androidx.wear.compose.material.ToggleButtonDefaults
import androidx.wear.compose.material.rememberRevealState
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.ButtonSize
import com.google.android.horologist.compose.material.Confirmation
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.features.applications.ApplicationsViewModel
import com.kgurgul.cpuinfo.features.applications.registerUninstallListener
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.apps_show_system_apps
import com.kgurgul.cpuinfo.shared.apps_sort_order
import com.kgurgul.cpuinfo.shared.apps_uninstall
import com.kgurgul.cpuinfo.shared.ic_apk_document_filled
import com.kgurgul.cpuinfo.shared.ic_apk_document_outlined
import com.kgurgul.cpuinfo.shared.refresh
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuProgressIndicator
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearApplicationsScreen(
    swipeToDismissBoxState: SwipeToDismissBoxState,
    viewModel: ApplicationsViewModel = koinViewModel(),
) {
    registerUninstallListener(
        onRefresh = viewModel::onRefreshApplications,
    )
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearApplicationsScreen(
        uiState = uiState,
        swipeToDismissBoxState = swipeToDismissBoxState,
        onAppClicked = viewModel::onApplicationClicked,
        onRefreshApplications = viewModel::onRefreshApplications,
        onSnackbarDismissed = viewModel::onSnackbarDismissed,
        onAppUninstallClicked = viewModel::onAppUninstallClicked,
        onAppSettingsClicked = viewModel::onAppSettingsClicked,
        onSystemAppsSwitched = viewModel::onSystemAppsSwitched,
        onSortOrderChange = viewModel::onSortOrderChange,
    )
}

@Composable
fun WearApplicationsScreen(
    uiState: ApplicationsViewModel.UiState,
    swipeToDismissBoxState: SwipeToDismissBoxState,
    onAppClicked: (packageName: String) -> Unit,
    onRefreshApplications: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    onSortOrderChange: (ascending: Boolean) -> Unit,
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
                        text = stringResource(Res.string.applications),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            items(
                items = uiState.applications,
                key = { item -> item.packageName },
            ) { item ->
                val revealState = rememberRevealState()
                val coroutineScope = rememberCoroutineScope()
                val uninstallText = stringResource(Res.string.apps_uninstall)
                val settingsText = stringResource(Res.string.settings)
                SwipeToRevealChip(
                    revealState = revealState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .edgeSwipeToDismiss(swipeToDismissBoxState)
                        .semantics {
                            customActions = listOf(
                                CustomAccessibilityAction(uninstallText) {
                                    onAppUninstallClicked(item.packageName)
                                    true
                                },
                                CustomAccessibilityAction(settingsText) {
                                    onAppSettingsClicked(item.packageName)
                                    true
                                }
                            )
                        },
                    primaryAction = {
                        SwipeToRevealPrimaryAction(
                            revealState = revealState,
                            icon = {
                                Icon(
                                    imageVector = SwipeToRevealDefaults.Delete,
                                    contentDescription = uninstallText,
                                )
                            },
                            label = { Text(uninstallText) },
                            onClick = { onAppUninstallClicked(item.packageName) },
                        )
                    },
                    secondaryAction = {
                        SwipeToRevealSecondaryAction(
                            revealState = revealState,
                            onClick = { onAppSettingsClicked(item.packageName) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = settingsText,
                            )
                        }
                    },
                    onFullSwipe = {
                        onAppUninstallClicked(item.packageName)
                        coroutineScope.launch {
                            revealState.animateTo(RevealValue.Covered)
                        }
                    }
                ) {
                    val secondaryLabel = buildString {
                        append(item.packageName)
                        append("\n")
                        append(item.versionName)
                    }
                    WearCpuChip(
                        modifier = Modifier.fillMaxWidth(),
                        label = item.name,
                        secondaryLabel = secondaryLabel,
                        secondaryLabelMaxLines = 4,
                        icon = {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(item.appIconUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(ChipDefaults.IconSize)
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        },
                        onClick = { onAppClicked(item.packageName) },
                    )
                }
            }
            if (!uiState.isLoading) {
                item(key = "__buttons") {
                    Row(
                        horizontalArrangement = Arrangement
                            .spacedBy(12.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Button(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.refresh),
                            onClick = onRefreshApplications,
                            buttonSize = ButtonSize.Small,
                        )
                        ToggleButton(
                            checked = uiState.withSystemApps,
                            onCheckedChange = { onSystemAppsSwitched(!uiState.withSystemApps) },
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        ) {
                            val systemAppIcon = if (uiState.withSystemApps) {
                                Res.drawable.ic_apk_document_filled
                            } else {
                                Res.drawable.ic_apk_document_outlined
                            }
                            Icon(
                                painter = painterResource(systemAppIcon),
                                contentDescription = stringResource(
                                    Res.string.apps_show_system_apps
                                ),
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.SmallIconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        }
                        ToggleButton(
                            checked = uiState.isSortAscending,
                            onCheckedChange = { onSortOrderChange(!uiState.isSortAscending) },
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        ) {
                            val sortingIcon = if (uiState.isSortAscending) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.Default.KeyboardArrowUp
                            }
                            Icon(
                                imageVector = sortingIcon,
                                contentDescription = stringResource(Res.string.apps_sort_order),
                                modifier = Modifier
                                    .size(ToggleButtonDefaults.SmallIconSize)
                                    .wrapContentSize(align = Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
        if (uiState.isLoading) {
            WearCpuProgressIndicator()
        }
        uiState.snackbarMessage?.let {
            AppOpeningConfirmation(
                message = stringResource(it),
                onTimeout = onSnackbarDismissed,
            )
        }
    }
}

@Composable
private fun AppOpeningConfirmation(
    message: String,
    onTimeout: () -> Unit,
) {
    Confirmation(
        onTimeout = onTimeout,
        icon = {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
        },
        durationMillis = 2000L,
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center
        )
    }
}
