@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.kgurgul.cpuinfo.features.information

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.LayoutDirection
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.ANDROID_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.CPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.GPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.HARDWARE_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.INFO_PAGE_AMOUNT
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.RAM_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SCREEN_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SENSORS_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.STORAGE_POS
import com.kgurgul.cpuinfo.features.information.cpu.TvCpuInfoScreen
import com.kgurgul.cpuinfo.features.information.gpu.TvGpuInfoScreen
import com.kgurgul.cpuinfo.features.information.hardware.TvHardwareInfoScreen
import com.kgurgul.cpuinfo.features.information.os.TvOsInfoScreen
import com.kgurgul.cpuinfo.features.information.ram.TvRamInfoScreen
import com.kgurgul.cpuinfo.features.information.screen.TvScreenInfoScreen
import com.kgurgul.cpuinfo.features.information.sensors.TvSensorsInfoScreen
import com.kgurgul.cpuinfo.features.information.storage.TvStorageInfoScreen
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvInfoContainerScreen(
    viewModel: InfoContainerViewModel = koinViewModel(),
) {
    TvInfoContainerScreen(
        onPageChanged = viewModel::onPageChanged,
    )
}

@Composable
fun TvInfoContainerScreen(
    onPageChanged: (Int) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        InfoContainer(
            paddingValues = paddingValues,
            onPageChanged = onPageChanged,
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}

@Composable
private fun InfoContainer(
    paddingValues: PaddingValues,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabTitles = (0 until INFO_PAGE_AMOUNT).map { getTabTitle(position = it) }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .then(modifier),
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions, doesTabRowHaveFocus ->
                TabRowDefaults.PillIndicator(
                    currentTabPosition = tabPositions[selectedTabIndex],
                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                    activeColor = MaterialTheme.colorScheme.surfaceTint,
                    inactiveColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.4f),
                )
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .focusRestorer()
                .padding(spacingMedium)
        ) {
            tabTitles.forEachIndexed { index, tab ->
                key(index) {
                    Tab(
                        selected = index == selectedTabIndex,
                        onFocus = { selectedTabIndex = index },
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                horizontal = spacingMedium,
                                vertical = spacingSmall,
                            )
                        )
                    }
                }
            }
        }
        AnimatedContent(
            targetState = selectedTabIndex,
            label = "screen_transition",
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
            modifier = Modifier.padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            ),
        ) { targetPos ->
            when (targetPos) {
                CPU_POS -> TvCpuInfoScreen()
                GPU_POS -> TvGpuInfoScreen()
                RAM_POS -> TvRamInfoScreen()
                STORAGE_POS -> TvStorageInfoScreen()
                SCREEN_POS -> TvScreenInfoScreen()
                ANDROID_POS -> TvOsInfoScreen()
                HARDWARE_POS -> TvHardwareInfoScreen()
                SENSORS_POS -> TvSensorsInfoScreen()
                else -> throw IllegalArgumentException("Unknown position")
            }
            onPageChanged(targetPos)
        }
    }
}
