@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.kgurgul.cpuinfo.features.information

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.INFO_PAGE_AMOUNT
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.shared.running_gc
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvInfoContainerScreen(
    viewModel: InfoContainerViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvInfoContainerScreen(
        uiState = uiState,
        onRamCleanupClicked = viewModel::onClearRamClicked,
        onPageChanged = viewModel::onPageChanged,
    )
}

@Composable
fun TvInfoContainerScreen(
    uiState: InfoContainerViewModel.UiState,
    onRamCleanupClicked: () -> Unit,
    onPageChanged: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.hardware),
                actions = {
                    AnimatedVisibility(
                        visible = uiState.isRamCleanupVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = onRamCleanupClicked) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.running_gc),
                            )
                        }
                    }
                },
            )
        },
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
    val pagerState = rememberPagerState(pageCount = { INFO_PAGE_AMOUNT })
    val scrollCoroutineScope = rememberCoroutineScope()
    val tabTitles = (0 until INFO_PAGE_AMOUNT).map { getTabTitle(position = it) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> onPageChanged(page) }
    }
    val tabs = listOf("Tab 1", "Tab 2", "Tab 3")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .then(modifier),
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.focusRestorer()
        ) {
            tabs.forEachIndexed { index, tab ->
                key(index) {
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        /*SecondaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = maxOf(
                paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            ),
            divider = {},
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            indicator = {
                SecondaryIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .tabIndicatorOffset(pagerState.currentPage),
                )
            },
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scrollCoroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) },
                )
            }
        }

        HorizontalScrollbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp),
            scrollState = scrollState,
        )
    }*/

        /* HorizontalPager(
        state = pagerState,
        pageContent = {
            when (it) {
                CPU_POS -> CpuInfoScreen()
                GPU_POS -> GpuInfoScreen()
                RAM_POS -> RamInfoScreen()
                STORAGE_POS -> StorageInfoScreen()
                SCREEN_POS -> ScreenInfoScreen()
                ANDROID_POS -> OsInfoScreen()
                HARDWARE_POS -> HardwareInfoScreen()
                SENSORS_POS -> SensorsInfoScreen()
                else -> throw IllegalArgumentException("Unknown position")
            }
        },
        modifier = Modifier.padding(
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
        ),
    )*/
    }
}
