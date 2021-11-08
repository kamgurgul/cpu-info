package com.kgurgul.cpuinfo.features.applications

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.wrappers.Result

@Composable
fun ApplicationsScreen(viewModel: NewApplicationsViewModel = viewModel()) {
    val uiState by viewModel.applicationList.collectAsState()
    val isRefreshingState = uiState is Result.InProgress
    Surface {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshingState),
            onRefresh = { viewModel.refreshApplications() },
        ) {
            (uiState as? Result.Success)?.let {
                ApplicationsList(state = it)
            }
        }
    }
}

@Composable
fun ApplicationsList(state: Result.Success<List<ExtendedApplicationData>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(state.data) {
            ApplicationItem(appData = it)
        }
    }
}

@Composable
fun ApplicationItem(appData: ExtendedApplicationData) {
    Text(text = appData.name)
}

@Preview
@Composable
fun ApplicationInfoPreviewLight() {
    CpuInfoTheme {
        Surface {
            ApplicationItem(testAppData)
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ApplicationInfoPreviewDark() {
    CpuInfoTheme {
        Surface {
            ApplicationItem(testAppData)
        }
    }
}

private val testAppData = ExtendedApplicationData(
    "Cpu Info",
    "com.kgurgul.cpuinfo",
    "/testDir",
    null,
    false,
    Uri.parse("uri://ap_icon")
)