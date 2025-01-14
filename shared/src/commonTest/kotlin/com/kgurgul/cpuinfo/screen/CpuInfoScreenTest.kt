package com.kgurgul.cpuinfo.screen

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoScreen
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import kotlin.test.Test

class CpuInfoScreenTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun hasItems() = runComposeUiTest {
        setContent {
            CpuInfoScreen(
                uiState = CpuInfoViewModel.UiState(
                    cpuData = TestData.cpuData
                )
            )
        }
        listOf(
            TestData.cpuData.processorName,
            TestData.cpuData.abi,
            TestData.cpuData.coreNumber.toString(),
        ).forEach { tag ->
            onNodeWithText(tag).performScrollTo().assertIsDisplayed()
        }
    }
}
