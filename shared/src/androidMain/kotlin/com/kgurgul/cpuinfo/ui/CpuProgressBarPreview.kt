package com.kgurgul.cpuinfo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun CpuProgressBarPreview() {
    CpuInfoTheme {
        CpuProgressBar(
            label = "Label",
            progress = 0.1f,
            minMaxValues = "Min" to "Max",
            prefixImageRes = null,
        )
    }
}