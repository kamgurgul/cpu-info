package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.data.local.StubUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.features.temperature.TemperatureScreen
import com.kgurgul.cpuinfo.features.temperature.TemperatureViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
fun TemperatureScreenPreview() {
    CpuInfoTheme {
        TemperatureScreen(
            uiState = TemperatureViewModel.UiState(
                temperatureFormatter = TemperatureFormatter(StubUserPreferencesRepository()),
                isLoading = false,
                temperatureItems = persistentListOf(
                    TemperatureItem(
                        id = 0,
                        icon = Res.drawable.ic_cpu_temp,
                        name = "CPU",
                        temperature = 30f
                    ),
                    TemperatureItem(
                        id = 1,
                        icon = Res.drawable.ic_battery,
                        name = "Battery",
                        temperature = 30f
                    )
                )
            ),
        )
    }
}
