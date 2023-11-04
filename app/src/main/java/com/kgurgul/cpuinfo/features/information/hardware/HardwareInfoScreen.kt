package com.kgurgul.cpuinfo.features.information.hardware

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun HardwareInfoScreen(
    viewModel: HardwareInfoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction("android.intent.action.ACTION_POWER_CONNECTED")
            addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
        }
        val powerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                viewModel.refreshHardwareInfo()
            }
        }
        ContextCompat.registerReceiver(context, powerReceiver, filter, RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(powerReceiver)
        }
    }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    HardwareInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun HardwareInfoScreen(
    uiState: HardwareInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            uiState.hardwareItems
        ) { index, (title, value) ->
            InformationRow(
                title = title,
                value = value,
                isLastItem = index == uiState.hardwareItems.lastIndex
            )
        }
    }
}

@Preview
@Composable
fun HardwareInfoScreenPreview() {
    CpuInfoTheme {
        HardwareInfoScreen(
            uiState = HardwareInfoViewModel.UiState(
                listOf(
                    "test" to "",
                    "test" to "test",
                )
            ),
        )
    }
}
