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
        setContent { CpuInfoScreen(uiState = CpuInfoViewModel.UiState(cpuData = TestData.cpuData)) }
        listOf("CPU_NAME", "x64", "1")
            .forEach { text -> onNodeWithText(text).performScrollTo().assertIsDisplayed() }
    }
}
