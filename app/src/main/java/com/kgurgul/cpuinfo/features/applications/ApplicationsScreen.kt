package com.kgurgul.cpuinfo.features.applications

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.theme.CpuInfoTheme
import kotlinx.coroutines.launch

@Composable
fun ApplicationsScreen(
    uiState: NewApplicationsViewModel.UiState,
    onAppClicked: (packageName: String) -> Unit,
    onRefreshApplications: () -> Unit,
    onSnackbarDismissed: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(uiState.snackbarMessage) {
        scope.launch {
            if (uiState.snackbarMessage != -1) {
                val result = scaffoldState.snackbarHostState.showSnackbar(
                    context.getString(uiState.snackbarMessage)
                )
                if (result == SnackbarResult.Dismissed) {
                    onSnackbarDismissed()
                }
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
    ) { innerPaddingModifier ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(uiState.isLoading),
            onRefresh = { onRefreshApplications() },
            modifier = Modifier.padding(innerPaddingModifier),
        ) {
            ApplicationsList(
                appList = uiState.applications,
                onAppClicked = onAppClicked
            )
        }
    }
}

@Composable
fun ApplicationsList(
    appList: List<ExtendedApplicationData>,
    onAppClicked: (packageName: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            items = appList,
            key = { app -> app.packageName }
        ) {
            ApplicationItem(
                appData = it,
                onAppClicked = onAppClicked
            )
        }
    }
}

@Composable
fun ApplicationItem(
    appData: ExtendedApplicationData,
    onAppClicked: (packageName: String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onAppClicked(appData.packageName) })
            .padding(8.dp),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(appData.appIconUri)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
        )
        Column(
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(appData.name)
            Text(
                text = appData.packageName,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Preview
@Composable
fun ApplicationInfoPreviewLight() {
    CpuInfoTheme {
        Surface {
            ApplicationItem(previewAppData) {}
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ApplicationInfoPreviewDark() {
    CpuInfoTheme {
        Surface {
            ApplicationItem(previewAppData) {}
        }
    }
}

private val previewAppData = ExtendedApplicationData(
    "Cpu Info",
    "com.kgurgul.cpuinfo",
    "/testDir",
    null,
    false,
    Uri.parse("https://avatars.githubusercontent.com/u/6407041?s=32&v=4")
)