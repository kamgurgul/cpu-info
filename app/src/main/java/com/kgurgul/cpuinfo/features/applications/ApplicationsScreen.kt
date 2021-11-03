package com.kgurgul.cpuinfo.features.applications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.utils.wrappers.Result
import timber.log.Timber

@Composable
fun ApplicationsScreen(newApplicationsViewModel: NewApplicationsViewModel = viewModel()) {
    val uiState by newApplicationsViewModel.applicationList.collectAsState()
    ApplicationsList(state = uiState)
}

@Composable
fun ApplicationsList(state: Result<List<ExtendedApplicationData>>) {
    Timber.d(state.toString())
}
