package com.kgurgul.cpuinfo.wear.features.information

import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.pager.rememberPagerState
import com.google.android.horologist.compose.pager.PagerScreen
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.ANDROID_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.CPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.GPU_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.HARDWARE_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.INFO_PAGE_AMOUNT
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.RAM_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SCREEN_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.SENSORS_POS
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel.Companion.STORAGE_POS
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoScreen
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoScreen
import com.kgurgul.cpuinfo.features.information.os.OsInfoScreen
import com.kgurgul.cpuinfo.features.information.ram.RamInfoScreen
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoScreen
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoScreen
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.cpu.WearCpuInfoScreen


@Composable
fun WearInfoContainerScreen() {
    val pagerState = rememberPagerState(pageCount = { INFO_PAGE_AMOUNT })
    PagerScreen(
        state = pagerState,
    ) {
        when (it) {
            CPU_POS -> WearCpuInfoScreen()
            GPU_POS -> GpuInfoScreen()
            RAM_POS -> RamInfoScreen()
            STORAGE_POS -> StorageInfoScreen()
            SCREEN_POS -> ScreenInfoScreen()
            ANDROID_POS -> OsInfoScreen()
            HARDWARE_POS -> HardwareInfoScreen()
            SENSORS_POS -> SensorsInfoScreen()
            else -> throw IllegalArgumentException("Unknown position")
        }
    }
}
