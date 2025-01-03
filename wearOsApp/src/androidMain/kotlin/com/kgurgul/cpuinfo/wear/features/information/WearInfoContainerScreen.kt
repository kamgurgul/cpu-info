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
import com.kgurgul.cpuinfo.wear.features.information.cpu.WearCpuInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.gpu.WearGpuInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.hardware.WearHardwareInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.os.WearOsInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.ram.WearRamInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.screen.WearScreenInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.sensors.WearSensorsInfoScreen
import com.kgurgul.cpuinfo.wear.features.information.storage.WearStorageInfoScreen


@Composable
fun WearInfoContainerScreen() {
    val pagerState = rememberPagerState(pageCount = { INFO_PAGE_AMOUNT })
    PagerScreen(
        state = pagerState,
    ) {
        when (it) {
            CPU_POS -> WearCpuInfoScreen()
            GPU_POS -> WearGpuInfoScreen()
            RAM_POS -> WearRamInfoScreen()
            STORAGE_POS -> WearStorageInfoScreen()
            SCREEN_POS -> WearScreenInfoScreen()
            ANDROID_POS -> WearOsInfoScreen()
            HARDWARE_POS -> WearHardwareInfoScreen()
            SENSORS_POS -> WearSensorsInfoScreen()
            else -> throw IllegalArgumentException("Unknown position")
        }
    }
}
