package com.kgurgul.cpuinfo.features.information.sensors

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import com.kgurgul.cpuinfo.utils.collectAsStateMultiplatform
import org.koin.androidx.compose.koinViewModel

@Composable
fun SensorsInfoScreen(
    viewModel: SensorsInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateMultiplatform()
    SensorsInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun SensorsInfoScreen(
    uiState: SensorsInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            uiState.sensors,
            key = { _, sensorData -> sensorData.id }
        ) { index, sensorData ->
            SensorItem(
                title = sensorData.name,
                value = sensorData.value,
                isLastItem = index == uiState.sensors.lastIndex
            )
        }
    }
}

@Composable
private fun SensorItem(
    title: String,
    value: String,
    isLastItem: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .focusable(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.size(spacingXSmall))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .7f),
        )
    }
    if (!isLastItem) {
        CpuDivider(
            modifier = Modifier.padding(top = spacingSmall),
        )
    }
}
