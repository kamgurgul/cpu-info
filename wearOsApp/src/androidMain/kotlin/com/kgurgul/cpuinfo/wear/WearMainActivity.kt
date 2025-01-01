package com.kgurgul.cpuinfo.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.wear.features.WearHostScreen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class WearMainActivity : ComponentActivity() {

    private val viewModel: HostViewModel by viewModel()

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

        setContent {
            WearHostScreen(
                viewModel = viewModel,
            )
        }
    }
}
