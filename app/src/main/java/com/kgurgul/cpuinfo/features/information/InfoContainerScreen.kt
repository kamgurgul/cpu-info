package com.kgurgul.cpuinfo.features.information

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.information.android.AndroidInfoScreen
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoScreen
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoScreen
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoScreen
import com.kgurgul.cpuinfo.features.information.ram.RamInfoScreen
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoScreen
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoScreen
import com.kgurgul.cpuinfo.features.information.storage.StorageScreen
import kotlinx.coroutines.launch

@Composable
fun InfoContainerScreen() {
    val pagerState = rememberPagerState(pageCount = { INFO_PAGE_AMOUNT })
    val scrollCoroutineScope = rememberCoroutineScope()
    val tabTitles = (0 until INFO_PAGE_AMOUNT).map { getTabTitle(position = it) }
    Column(
        modifier = Modifier.fillMaxSize()
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
fun getTabTitle(position: Int) = stringResource(
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

private const val CPU_POS = 0
private const val GPU_POS = 1
private const val RAM_POS = 2
private const val STORAGE_POS = 3
private const val SCREEN_POS = 4
private const val ANDROID_POS = 5
private const val HARDWARE_POS = 6
private const val SENSORS_POS = 7

private const val INFO_PAGE_AMOUNT = 8
