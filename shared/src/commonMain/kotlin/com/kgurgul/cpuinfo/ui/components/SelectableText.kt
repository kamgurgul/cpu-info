package com.kgurgul.cpuinfo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
expect fun SelectableText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier
)
