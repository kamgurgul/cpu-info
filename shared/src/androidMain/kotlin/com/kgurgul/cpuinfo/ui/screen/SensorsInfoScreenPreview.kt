package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoScreen
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
fun SensorsInfoScreenPreview() {
    CpuInfoTheme {
        SensorsInfoScreen(
            uiState = SensorsInfoViewModel.UiState(
                persistentListOf(
                    SensorData(
                        id = "test",
                        name = TextResource.Text("test"),
                        value = "",
                    ),
                    SensorData(
                        id = "test",
                        name = TextResource.Text("test"),
                        value = "test",
                    ),
                ),
            ),
        )
    }
}
