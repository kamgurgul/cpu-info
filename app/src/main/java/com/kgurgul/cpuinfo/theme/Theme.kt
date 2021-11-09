package com.kgurgul.cpuinfo.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightThemeColors = lightColors(
    primary = Gray66,
    primaryVariant = Gray50,
    onPrimary = Color.White,
    secondary = RedLight,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = RedError,
    onError = Color.White
)

val DarkThemeColors = darkColors(
    primary = Gray28,
    primaryVariant = Color.Black,
    onPrimary = Color.White,
    secondary = RedDark,
    onSecondary = Color.White,
    background = Gray18,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = RedError,
    onError = Color.White
)

@Composable
fun CpuInfoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkThemeColors
    } else {
        LightThemeColors
    }
    MaterialTheme(
        colors = colors,
        typography = CpuInfoTypography,
        content = content
    )
}
