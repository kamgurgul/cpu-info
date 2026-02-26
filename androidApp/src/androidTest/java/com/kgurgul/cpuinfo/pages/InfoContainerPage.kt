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
package com.kgurgul.cpuinfo.pages

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.ui.components.TEST_TAG_CPU_TOP_APP_BAR
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

class InfoContainerPage(composeContent: ComposeContentTestRule) {

    private val toolbarTitle = composeContent.onNodeWithTag(TEST_TAG_CPU_TOP_APP_BAR)
    private val cpuTab = composeContent.onNodeWithText(runBlocking { getString(Res.string.cpu) })

    fun assertViewDisplayed() {
        toolbarTitle.assertIsDisplayed()
        cpuTab.assertIsDisplayed()
    }
}
