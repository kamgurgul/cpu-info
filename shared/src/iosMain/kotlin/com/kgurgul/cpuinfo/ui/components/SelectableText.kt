package com.kgurgul.cpuinfo.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
actual fun SelectableText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}
