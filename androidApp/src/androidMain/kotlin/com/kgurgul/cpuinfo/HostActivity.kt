package com.kgurgul.cpuinfo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.ui.shouldUseDarkTheme
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.DarkColors
import com.kgurgul.cpuinfo.ui.theme.LightColors
import com.kgurgul.cpuinfo.ui.theme.darkPrimary
import com.kgurgul.cpuinfo.ui.theme.lightPrimary
import com.kgurgul.cpuinfo.utils.AndroidShortcutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HostActivity : ComponentActivity() {

    private val viewModel: HostViewModel by viewModel()
    private val androidShortcutManager: AndroidShortcutManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: HostViewModel.UiState by mutableStateOf(HostViewModel.UiState())
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow
                    .onEach { uiState = it }
                    .collect()
            }
        }
        splashScreen.setKeepOnScreenCondition { uiState.isLoading }

        enableEdgeToEdge()

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)
            val systemBarScrim = (if (darkTheme) darkPrimary else lightPrimary).toArgb()
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(systemBarScrim),
                    navigationBarStyle = SystemBarStyle.dark(systemBarScrim),
                )
                onDispose {}
            }
            val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            val colors = when {
                dynamicColor && darkTheme -> {
                    val dynamicDarkColors = dynamicDarkColorScheme(LocalContext.current)
                    DarkColors.copy(
                        secondary = dynamicDarkColors.surfaceContainerHighest,
                        onSecondary = dynamicDarkColors.onSurface,
                    )
                }

                dynamicColor && !darkTheme -> {
                    val dynamicLightColors = dynamicLightColorScheme(LocalContext.current)
                    LightColors.copy(
                        secondary = dynamicLightColors.surfaceContainerHighest,
                        onSecondary = dynamicLightColors.onSurface,
                    )
                }

                darkTheme -> DarkColors
                else -> LightColors
            }
            LaunchedEffect(uiState.isApplicationSectionVisible) {
                androidShortcutManager.createShortcuts(uiState.isApplicationSectionVisible)
            }
            CpuInfoTheme(
                useDarkTheme = darkTheme,
                colors = colors,
            ) {
                HostScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}
