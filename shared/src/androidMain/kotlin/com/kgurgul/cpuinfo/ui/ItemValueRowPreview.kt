package com.kgurgul.cpuinfo.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ItemValueRowPreview() {
    CpuInfoTheme {
        ItemValueRow(
            title = "Title",
            value = "Value",
        )
    }
}