package com.kgurgul.cpuinfo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.ui.components.CpuCheckbox
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun CpuSwitchBoxPreview() {
    CpuInfoTheme {
        CpuCheckbox(
            text = "Test",
            isChecked = true,
            onCheckedChange = {}
        )
    }
}