package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kgurgul.cpuinfo.ui.theme.spacingMedium

@Composable
fun CpuSnackbar(snackbarData: SnackbarData) {
    Snackbar(
        modifier = Modifier.padding(spacingMedium),
        snackbarData = snackbarData,
    )
}