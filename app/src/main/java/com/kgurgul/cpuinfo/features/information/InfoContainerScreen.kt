package com.kgurgul.cpuinfo.features.information

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.ANDROID_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.CPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.GPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.HARDWARE_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.INFO_PAGE_AMOUNT
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.RAM_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SCREEN_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SENSORS_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.STORAGE_POS
import com.kgurgul.cpuinfo.features.information.android.AndroidInfoScreen
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoScreen
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoScreen
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoScreen
import com.kgurgul.cpuinfo.features.information.ram.RamInfoScreen
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoScreen
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoScreen
import com.kgurgul.cpuinfo.features.information.storage.StorageScreen
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import kotlinx.coroutines.launch

@Composable
fun InfoContainerScreen(
    viewModel: InfoContainerViewModel = hiltViewModel()
) {
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
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(id = R.string.hardware),
                windowInsets = WindowInsets(0, 0, 0, 0),
                actions = {
                    AnimatedVisibility(
                        visible = uiState.isRamCleanupVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = onRamCleanupClicked) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(id = R.string.running_gc)
                            )
                        }
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        InfoContainer(
            onPageChanged = onPageChanged,
            modifier = paddingModifier,
        )
    }

}

@Composable
private fun InfoContainer(
    onPageChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { INFO_PAGE_AMOUNT })
    val scrollCoroutineScope = rememberCoroutineScope()
    val tabTitles = (0 until INFO_PAGE_AMOUNT).map { getTabTitle(position = it) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> onPageChanged(page) }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            divider = {},
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                )
            },
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scrollCoroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            pageContent = {
                when (it) {
                    CPU_POS -> CpuInfoScreen()
                    GPU_POS -> GpuInfoScreen()
                    RAM_POS -> RamInfoScreen()
                    STORAGE_POS -> StorageScreen()
                    SCREEN_POS -> ScreenInfoScreen()
                    ANDROID_POS -> AndroidInfoScreen()
                    HARDWARE_POS -> HardwareInfoScreen()
                    SENSORS_POS -> SensorsInfoScreen()
                    else -> throw IllegalArgumentException("Unknown position")
                }
            }
        )
    }
}

@Composable
private fun getTabTitle(position: Int) = stringResource(
    id = when (position) {
        CPU_POS -> R.string.cpu
        GPU_POS -> R.string.gpu
        RAM_POS -> R.string.ram
        STORAGE_POS -> R.string.storage
        SCREEN_POS -> R.string.screen
        ANDROID_POS -> R.string.android
        HARDWARE_POS -> R.string.hardware
        SENSORS_POS -> R.string.sensors
        else -> throw IllegalArgumentException("Unknown position")
    }
)
