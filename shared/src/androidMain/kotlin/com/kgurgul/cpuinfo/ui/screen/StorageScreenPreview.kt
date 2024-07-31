package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoViewModel
import com.kgurgul.cpuinfo.features.information.storage.StorageScreen
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun StorageScreenPreview() {
    CpuInfoTheme {
        StorageScreen(
            uiState = StorageInfoViewModel.UiState(
                storageItems = listOf(
                    StorageItem(
                        id = "0",
                        label = "Internal",
                        iconDrawable = Res.drawable.baseline_folder_special_24,
                        storageTotal = 100,
                        storageUsed = 50,
                    )
                )
            )
        )
    }
}