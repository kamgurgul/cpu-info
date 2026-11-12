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
package com.kgurgul.cpuinfo.features.information

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.ANDROID_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.CPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.GPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.HARDWARE_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.INFO_PAGE_AMOUNT
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.RAM_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SCREEN_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SENSORS_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.STORAGE_POS
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoScreen
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoScreen
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoScreen
import com.kgurgul.cpuinfo.features.information.os.OsInfoScreen
import com.kgurgul.cpuinfo.features.information.ram.RamInfoScreen
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoScreen
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoScreen
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoScreen
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.gpu
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.shared.ram
import com.kgurgul.cpuinfo.shared.running_gc
import com.kgurgul.cpuinfo.shared.screen
import com.kgurgul.cpuinfo.shared.sensors
import com.kgurgul.cpuinfo.shared.storage
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.ui.components.CpuActionToolbar
import com.kgurgul.cpuinfo.ui.components.CpuActionToolbarContentPadding
import com.kgurgul.cpuinfo.ui.components.HorizontalScrollbar
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable data object InformationRoute : NavKey

fun EntryProviderScope<NavKey>.informationScreen() {
    entry<InformationRoute> { InfoContainerScreen() }
}

@Composable
fun InfoContainerScreen(viewModel: InfoContainerViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    InfoContainerScreen(
        uiState = uiState,
        onRamCleanupClicked = viewModel::onClearRamClicked,
        onPageChanged = viewModel::onPageChanged,
    )
}

@Composable
fun InfoContainerScreen(
    uiState: InfoContainerViewModel.UiState,
    onRamCleanupClicked: () -> Unit,
    onPageChanged: (Int) -> Unit,
) {
    Scaffold(
        contentWindowInsets =
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        InfoContainer(
            paddingValues = paddingValues,
            onPageChanged = onPageChanged,
            isRamCleanupVisible = uiState.isRamCleanupVisible,
            onRamCleanupClicked = onRamCleanupClicked,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun InfoContainer(
    paddingValues: PaddingValues,
    isRamCleanupVisible: Boolean,
    onRamCleanupClicked: () -> Unit,
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { INFO_PAGE_AMOUNT })
    val scrollCoroutineScope = rememberCoroutineScope()
    val tabTitles = (0 until INFO_PAGE_AMOUNT).map { getTabTitle(position = it) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> onPageChanged(page) }
    }
    Column(modifier = modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier =
                Modifier.background(MaterialTheme.colorScheme.surface)
                    .padding(top = paddingValues.calculateTopPadding())
                    .fillMaxWidth()
        ) {
            SecondaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding =
                    maxOf(
                        paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ),
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = {
                    SecondaryIndicator(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                    )
                },
                scrollState = scrollState,
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scrollCoroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(text = title) },
                    )
                }
            }

            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp),
                scrollState = scrollState,
            )
        }

        HorizontalDivider()

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                pageContent = {
                    when (it) {
                        CPU_POS -> CpuInfoScreen()
                        GPU_POS -> GpuInfoScreen()
                        RAM_POS ->
                            RamInfoScreen(
                                topContentPadding =
                                    if (isRamCleanupVisible) CpuActionToolbarContentPadding
                                    else 0.dp
                            )

                        STORAGE_POS -> StorageInfoScreen()
                        SCREEN_POS -> ScreenInfoScreen()
                        ANDROID_POS -> OsInfoScreen()
                        HARDWARE_POS -> HardwareInfoScreen()
                        SENSORS_POS -> SensorsInfoScreen()
                        else -> throw IllegalArgumentException("Unknown position")
                    }
                },
                modifier =
                    Modifier.padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ),
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = isRamCleanupVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                CpuActionToolbar {
                    IconButton(onClick = onRamCleanupClicked) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.running_gc),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun getTabTitle(position: Int) =
    stringResource(
        when (position) {
            CPU_POS -> Res.string.cpu
            GPU_POS -> Res.string.gpu
            RAM_POS -> Res.string.ram
            STORAGE_POS -> Res.string.storage
            SCREEN_POS -> Res.string.screen
            ANDROID_POS -> Res.string.tab_os
            HARDWARE_POS -> Res.string.hardware
            SENSORS_POS -> Res.string.sensors
            else -> throw IllegalArgumentException("Unknown position")
        }
    )
