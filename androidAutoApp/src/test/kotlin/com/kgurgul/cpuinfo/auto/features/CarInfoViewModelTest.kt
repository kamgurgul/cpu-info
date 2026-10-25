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
package com.kgurgul.cpuinfo.auto.features

import app.cash.turbine.test
import com.kgurgul.cpuinfo.auto.data.provider.ICarDataProvider
import com.kgurgul.cpuinfo.auto.domain.model.CarData
import com.kgurgul.cpuinfo.auto.domain.model.CarEnergyLevelData
import com.kgurgul.cpuinfo.auto.domain.model.CarEnergyProfileData
import com.kgurgul.cpuinfo.auto.domain.model.CarHostData
import com.kgurgul.cpuinfo.auto.domain.model.CarMileageData
import com.kgurgul.cpuinfo.auto.domain.model.CarModelData
import com.kgurgul.cpuinfo.auto.domain.model.CarProperty
import com.kgurgul.cpuinfo.auto.domain.model.CarSpeedData
import com.kgurgul.cpuinfo.auto.domain.model.TollCardState
import com.kgurgul.cpuinfo.auto.domain.observable.CarDataObservable
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class CarInfoViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchersProvider =
        object : IDispatchersProvider {
            override val main: CoroutineDispatcher = testDispatcher
            override val io: CoroutineDispatcher = testDispatcher
            override val default: CoroutineDispatcher = testDispatcher
            override val unconfined: CoroutineDispatcher = testDispatcher
        }
    private val fakeCarDataProvider = FakeCarDataProvider()

    private lateinit var viewModel: CarInfoViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            CarInfoViewModel(
                carDataObservable =
                    CarDataObservable(
                        dispatchersProvider = dispatchersProvider,
                        carDataProvider = fakeCarDataProvider,
                    )
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiStateContainsAllCarData() = runTest {
        val expectedCarData =
            CarData(
                host = HOST,
                model = MODEL,
                speed = SPEED,
                tollCard = CarProperty.Unimplemented,
                missingPermissions = listOf(PERMISSION),
            )

        viewModel.uiStateFlow.test {
            assertEquals(
                CarInfoViewModel.UiState(isInitializing = false, carData = expectedCarData),
                expectMostRecentItem(),
            )
        }
    }

    @Test
    fun grantPermissionsClickSendsRequestEvent() = runTest {
        viewModel.uiStateFlow.test {
            expectMostRecentItem()
            viewModel.events.test {
                viewModel.onGrantPermissionsClick()

                assertEquals(
                    CarInfoViewModel.Event.RequestPermissions(listOf(PERMISSION)),
                    awaitItem(),
                )
            }
        }
    }

    @Test
    fun permissionsResultRefreshesData() = runTest {
        viewModel.uiStateFlow.test {
            expectMostRecentItem()
            fakeCarDataProvider.missingPermissions.value = emptyList()

            viewModel.onPermissionsResult()

            assertEquals(emptyList(), expectMostRecentItem().carData.missingPermissions)
        }
    }

    private class FakeCarDataProvider : ICarDataProvider {

        val missingPermissions = MutableStateFlow(listOf(PERMISSION))

        override fun getMissingPermissions(): List<String> = missingPermissions.value

        override fun observeHost(): Flow<CarHostData?> = flowOf(HOST)

        override fun observeModel(): Flow<CarModelData> = flowOf(MODEL)

        override fun observeEnergyProfile(): Flow<CarEnergyProfileData> = emptyFlow()

        override fun observeEnergyLevel(): Flow<CarEnergyLevelData> = emptyFlow()

        override fun observeSpeed(): Flow<CarSpeedData> = flowOf(SPEED)

        override fun observeMileage(): Flow<CarMileageData> = emptyFlow()

        override fun observeTollCard(): Flow<CarProperty<TollCardState>> =
            flowOf(CarProperty.Unimplemented)

        override fun observeCompass(): Flow<CarProperty<List<Float>>> = emptyFlow()

        override fun observeAccelerometer(): Flow<CarProperty<List<Float>>> = emptyFlow()

        override fun observeGyroscope(): Flow<CarProperty<List<Float>>> = emptyFlow()
    }

    companion object {
        private const val PERMISSION = "com.google.android.gms.permission.CAR_SPEED"
        private val HOST = CarHostData(carAppApiLevel = 7, hostPackageName = "host")
        private val MODEL =
            CarModelData(
                manufacturer = CarProperty.Available("Manufacturer"),
                name = CarProperty.Available("Name"),
                year = CarProperty.Available(2026),
            )
        private val SPEED =
            CarSpeedData(
                displaySpeedMetersPerSecond = CarProperty.Available(10f),
                rawSpeedMetersPerSecond = CarProperty.Available(10.1f),
            )
    }
}
