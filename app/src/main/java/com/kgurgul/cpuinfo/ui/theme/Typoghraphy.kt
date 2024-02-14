package com.kgurgul.cpuinfo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaultTextStyle = TextStyle.Default

val CpuInfoTypography = Typography(
    titleLarge = defaultTextStyle.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    ),
    labelSmall = defaultTextStyle.copy(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 12.sp,
    ),
)