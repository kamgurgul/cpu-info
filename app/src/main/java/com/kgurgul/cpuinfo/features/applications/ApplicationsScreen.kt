package com.kgurgul.cpuinfo.features.applications

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.wrappers.Result

@Composable
fun ApplicationsScreen(
    viewModel: NewApplicationsViewModel = viewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onAppClicked: (packageName: String) -> Unit
) {
    val uiState by viewModel.applicationList.collectAsState()

    val isRefreshingState = uiState is Result.Loading

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshingState),
            onRefresh = { viewModel.refreshApplications() },
        ) {
            (uiState as? Result.Success)?.let {
                ApplicationsList(
                    appList = it.data,
                    onAppClicked = onAppClicked
                )
            }
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
        items(appList) {
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
        Image(
            painter = rememberImagePainter(
                data = appData.appIconUri,
                builder = {
                    crossfade(true)
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
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