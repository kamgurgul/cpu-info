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
package com.kgurgul.cpuinfo.features.information.screen

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeScreenDataProvider
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.result.GetScreenDataInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.orientation
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest

class ScreenInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val items = TestData.itemValueRowData
    private val fakeScreenDataProvider = FakeScreenDataProvider(data = items)
    private val getScreenDataInteractor =
        GetScreenDataInteractor(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            screenDataProvider = fakeScreenDataProvider,
        )

    private lateinit var viewModel: ScreenInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = ScreenInfoViewModel(getScreenDataInteractor = getScreenDataInteractor)
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState =
            ScreenInfoViewModel.UiState(
                isInitializing = false,
                items =
                    persistentListOf(
                        ItemValue.Text("test", ""),
                        ItemValue.Text("test", "test"),
                        ItemValue.NameResource(Res.string.orientation, "Unknown"),
                    ),
            )

        viewModel.uiStateFlow.test { assertEquals(expectedUiState, awaitItem()) }
    }
}
