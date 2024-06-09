package com.kgurgul.cpuinfo.features.information.storage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import kotlin.math.roundToInt

@Composable
fun StorageScreen(
    viewModel: StorageInfoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
            addAction(Intent.ACTION_MEDIA_CHECKING)
            addAction(Intent.ACTION_MEDIA_EJECT)
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_NOFS)
            addAction(Intent.ACTION_MEDIA_REMOVED)
            addAction(Intent.ACTION_MEDIA_SHARED)
            addAction(Intent.ACTION_MEDIA_UNMOUNTABLE)
            addAction(Intent.ACTION_MEDIA_UNMOUNTED)
            addDataScheme("file")
        }
        val mountedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                viewModel.onRefreshStorage()
            }
        }
        ContextCompat.registerReceiver(context, mountedReceiver, filter, RECEIVER_EXPORTED)

        onDispose {
            context.unregisterReceiver(mountedReceiver)
        }
    }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    StorageScreen(
        uiState = uiState
    )
}

@Composable
fun StorageScreen(
    uiState: StorageInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            uiState.storageItems,
            key = { it.iconRes }
        ) { storageItem ->
            val label = stringResource(id = storageItem.labelRes)
            val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
            val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
            val progress = storageItem.storageUsed.toFloat() / storageItem.storageTotal.toFloat()
            val usedPercent = (progress * 100.0).roundToInt()
            val storageDesc = "$label: $usedReadable / $totalReadable ($usedPercent%)"
            CpuProgressBar(
                label = storageDesc,
                progress = progress,
                progressHeight = 32.dp,
                prefixImageRes = storageItem.iconRes,
                modifier = Modifier.focusable(),
            )
        }
    }
}

@Preview
@Composable
fun StorageScreenPreview() {
    CpuInfoTheme {
        StorageScreen(
            uiState = StorageInfoViewModel.UiState(
                storageItems = listOf(
                    StorageItem(
                        labelRes = R.string.internal,
                        iconRes = R.drawable.baseline_folder_special_24,
                        storageTotal = 100,
                        storageUsed = 50,
                    )
                )
            )
        )
    }
}