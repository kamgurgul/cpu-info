@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.dialog.Dialog
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
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.information
import com.kgurgul.cpuinfo.shared.menu
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
                        onInformationClicked = { navController.navigate("list") }
                    )
                }
                composable("list") {
                    ListScreen()
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
        }
    }
}

sealed class WearHostScreen(val route: String) {
    data object Menu : WearHostScreen("menu")
}

@Composable
fun ListScreen() {
    var showDialog by remember { mutableStateOf(false) }

    /*
     * Specifying the types of items that appear at the start and end of the list ensures that the
     * appropriate padding is used.
     */
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.SingleButton
        )
    )

    ScreenScaffold(scrollState = columnState) {
        /*
         * The Horologist [ScalingLazyColumn] takes care of the horizontal and vertical
         * padding for the list, so there is no need to specify it, as in the [GreetingScreen]
         * composable.
         */
        ScalingLazyColumn(
            columnState = columnState
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = "Header")
                }
            }
            item {
                TitleCard(title = { Text("Example Title") }, onClick = { }) {
                    Text("Example Content\nMore Lines\nAnd More")
                }
            }
            item {
                Chip(label = "Example Chip", onClick = { })
            }
            item {
                Button(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Example Button",
                    onClick = { showDialog = true }
                )
            }
        }
    }

    SampleDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onCancel = {},
        onOk = {}
    )
}

@Composable
fun SampleDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onOk: () -> Unit
) {
    val state = rememberResponsiveColumnState()

    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        scrollState = state.state
    ) {
        SampleDialogContent(onCancel, onDismiss, onOk)
    }
}

@Composable
fun SampleDialogContent(
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {
    AlertContent(
        icon = {},
        title = "Title",
        onCancel = {
            onCancel()
            onDismiss()
        },
        onOk = {
            onOk()
            onDismiss()
        }
    ) {
        item {
            Text(text = "An unknown error occurred during the request.")
        }
    }
}
