package com.kgurgul.cpuinfo.wear.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ProgressIndicatorDefaults

@Composable
fun WearCpuProgressIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        strokeWidth = ProgressIndicatorDefaults.FullScreenStrokeWidth,
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
            .clearAndSetSemantics { }
            .then(modifier),
    )
}
