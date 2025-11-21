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

import com.kgurgul.cpuinfo.components.FakeRamCleanupComponent
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class InfoContainerViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeRamCleanupComponent = FakeRamCleanupComponent()
    private val ramCleanupAction =
        RamCleanupAction(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            ramCleanupComponent = fakeRamCleanupComponent,
        )

    private lateinit var viewModel: InfoContainerViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        fakeRamCleanupComponent.reset()
        viewModel = InfoContainerViewModel(ramCleanupAction = ramCleanupAction)
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun onClearRamClicked() = runTest {
        viewModel.onClearRamClicked()

        assertTrue(fakeRamCleanupComponent.cleanupInvoked)
    }
}
