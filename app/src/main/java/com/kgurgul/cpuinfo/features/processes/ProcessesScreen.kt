package com.kgurgul.cpuinfo.features.processes

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import com.kgurgul.cpuinfo.utils.Utils
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ProcessesScreen(
    viewModel: ProcessesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ProcessesScreen(
        uiState = uiState
    )
}

@Composable
fun ProcessesScreen(uiState: ProcessesViewModel.UiState) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(id = R.string.processes),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        ProcessList(
            processes = uiState.processes,
            modifier = paddingModifier
        )
    }
}

@Composable
private fun ProcessList(
    processes: ImmutableList<ProcessItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        modifier = modifier,
    ) {
        itemsIndexed(
            processes,
            key = { _, process -> process.pid },
        ) { index, process ->
            ProcessItem(process)
            if (index < processes.lastIndex) {
                CpuDivider(
                    modifier = Modifier.padding(vertical = spacingSmall),
                )
            }
        }
    }
}

@Composable
private fun ProcessItem(item: ProcessItem) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacingXSmall),
        modifier = Modifier.focusable(),
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        DoubleTextRow(
            text1 = "PID: ${item.pid}",
            text2 = "PPID: ${item.ppid}",
        )
        DoubleTextRow(
            text1 = "NICENESS: ${item.niceness}",
            text2 = "USER: ${item.user}",
        )
        DoubleTextRow(
            text1 = "RSS: ${Utils.humanReadableByteCount(item.rss.toLong() * 1024)}",
            text2 = "VSZ: ${Utils.humanReadableByteCount(item.vsize.toLong() * 1024)}",
        )
    }
}

@Composable
private fun DoubleTextRow(
    text1: String,
    text2: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingSmall)
    ) {
        Text(
            text = text1,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = text2,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
fun ProcessesScreenPreview() {
    CpuInfoTheme {
        ProcessesScreen(
            uiState = ProcessesViewModel.UiState()
        )
    }
}
