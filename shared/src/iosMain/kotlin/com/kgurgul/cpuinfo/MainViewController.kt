/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.ui.shouldUseDarkTheme
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.navigation.IosNativeNavigation
import com.kgurgul.cpuinfo.utils.navigation.IosNativeNavigationRenderer
import com.kgurgul.cpuinfo.utils.navigation.LocalNativeNavigation
import org.koin.compose.viewmodel.koinViewModel

/**
 * @param nativeNavigationRenderer renderer of the native navigation elements (Liquid Glass) or null
 *   when all navigation elements should be drawn by Compose
 */
fun MainViewController(nativeNavigationRenderer: IosNativeNavigationRenderer?) =
    ComposeUIViewController {
        IOSComposeApp(nativeNavigationRenderer = nativeNavigationRenderer)
    }

@Composable
fun IOSComposeApp(
    nativeNavigationRenderer: IosNativeNavigationRenderer?,
    hostViewModel: HostViewModel = koinViewModel(),
) {
    val uiState by hostViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val darkTheme = shouldUseDarkTheme(uiState)
    val nativeNavigation =
        remember(nativeNavigationRenderer) { nativeNavigationRenderer?.let(::IosNativeNavigation) }
    LaunchedEffect(nativeNavigation, darkTheme) { nativeNavigation?.setDarkTheme(darkTheme) }
    CpuInfoTheme(useDarkTheme = darkTheme) {
        CompositionLocalProvider(LocalNativeNavigation provides nativeNavigation) {
            HostScreen(viewModel = hostViewModel)
        }
    }
}
