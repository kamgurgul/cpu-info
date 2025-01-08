@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberPickerState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Button
import com.kgurgul.cpuinfo.features.settings.SettingsViewModel
import com.kgurgul.cpuinfo.features.settings.getTemperatureUnit
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ok
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearTemperatureUnitPickerScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onTemperatureUnitSelected: () -> Unit,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearTemperatureUnitPickerScreen(
        uiState = uiState,
        onTemperatureUnitSelected = {
            viewModel.setTemperatureUnit(it)
            onTemperatureUnitSelected()
        },
    )
}

@Composable
fun WearTemperatureUnitPickerScreen(
    uiState: SettingsViewModel.UiState,
    onTemperatureUnitSelected: (Int) -> Unit,
) {
    val state = rememberPickerState(
        initialNumberOfOptions = uiState.temperatureDialogOptions.size,
        repeatItems = false,
    )
    //val contentDescription by remember { derivedStateOf { getTemperatureUnit(state.selectedOption) } }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 24.dp,
                bottom = 8.dp,
            ),
    ) {
        Picker(
            state = state,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 100.dp),
        ) {
            Text(
                text = getTemperatureUnit(uiState.temperatureDialogOptions[it]),
                style = MaterialTheme.typography.display2,
            )
        }
        Button(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(Res.string.ok),
            onClick = { onTemperatureUnitSelected(state.selectedOption) },
        )
    }
}
