package com.kgurgul.cpuinfo.features.applications

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.utils.wrappers.Result

@Composable
fun ApplicationsScreen(viewModel: NewApplicationsViewModel = viewModel()) {
    val uiState by viewModel.applicationList.collectAsState()
    Surface {
        val isRefreshingState = uiState is Result.InProgress
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshingState),
            onRefresh = { viewModel.refreshApplications() },
        ) {
            ApplicationsList(state = uiState)
        }
    }
}

@Composable
fun ApplicationsList(state: Result<List<ExtendedApplicationData>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state is Result.Success) {
            state.data.forEach {
                item {
                    Text(text = it.name)
                }
            }
        }
    }
}
