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
package com.kgurgul.cpuinfo.auto.data.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.CarUnit
import androidx.car.app.hardware.common.CarValue
import androidx.car.app.hardware.common.OnCarDataAvailableListener
import androidx.car.app.hardware.info.Accelerometer
import androidx.car.app.hardware.info.CarSensors
import androidx.car.app.hardware.info.Compass
import androidx.car.app.hardware.info.EnergyProfile
import androidx.car.app.hardware.info.Gyroscope
import androidx.car.app.hardware.info.TollCard
import androidx.core.content.ContextCompat
import co.touchlab.kermit.Logger
import com.kgurgul.cpuinfo.auto.data.CarContextHolder
import com.kgurgul.cpuinfo.auto.domain.model.CarEnergyLevelData
import com.kgurgul.cpuinfo.auto.domain.model.CarEnergyProfileData
import com.kgurgul.cpuinfo.auto.domain.model.CarHostData
import com.kgurgul.cpuinfo.auto.domain.model.CarMileageData
import com.kgurgul.cpuinfo.auto.domain.model.CarModelData
import com.kgurgul.cpuinfo.auto.domain.model.CarProperty
import com.kgurgul.cpuinfo.auto.domain.model.CarSpeedData
import com.kgurgul.cpuinfo.auto.domain.model.DistanceUnit
import com.kgurgul.cpuinfo.auto.domain.model.EvConnectorType
import com.kgurgul.cpuinfo.auto.domain.model.FuelType
import com.kgurgul.cpuinfo.auto.domain.model.SpeedUnit
import com.kgurgul.cpuinfo.auto.domain.model.TollCardState
import java.util.concurrent.Executor
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CarDataProvider(
    private val context: Context,
    private val carContextHolder: CarContextHolder,
) : ICarDataProvider {

    private val executor: Executor by lazy { ContextCompat.getMainExecutor(context) }

    private val isAutomotive by lazy {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
    }

    private val permissions: CarPermissions by lazy {
        if (isAutomotive) CarPermissions.AUTOMOTIVE else CarPermissions.PROJECTED
    }

    override fun getMissingPermissions(): List<String> =
        permissions.all.filterNot { isPermissionGranted(it) }

    override fun observeHost(): Flow<CarHostData?> =
        carContextHolder.carContext.map { carContext ->
            carContext?.let {
                CarHostData(
                    carAppApiLevel = it.carAppApiLevel,
                    hostPackageName = it.hostInfo?.packageName,
                )
            }
        }

    override fun observeModel(): Flow<CarModelData> =
        observeCarData(
            requiredPermission = permissions.model,
            onMissingPermission =
                CarModelData(
                    manufacturer = CarProperty.MissingPermission,
                    name = CarProperty.MissingPermission,
                    year = CarProperty.MissingPermission,
                ),
            onNoData =
                CarModelData(
                    manufacturer = CarProperty.Unknown,
                    name = CarProperty.Unknown,
                    year = CarProperty.Unknown,
                ),
            register = { manager, listener ->
                manager.carInfo.fetchModel(executor, listener)
                null
            },
            mapper = { model ->
                CarModelData(
                    manufacturer = model.manufacturer.toCarProperty(),
                    name = model.name.toCarProperty(),
                    year = model.year.toCarProperty(),
                )
            },
        )

    override fun observeEnergyProfile(): Flow<CarEnergyProfileData> =
        observeCarData(
            requiredPermission = permissions.energyProfile,
            onMissingPermission =
                CarEnergyProfileData(
                    fuelTypes = CarProperty.MissingPermission,
                    evConnectorTypes = CarProperty.MissingPermission,
                ),
            onNoData =
                CarEnergyProfileData(
                    fuelTypes = CarProperty.Unknown,
                    evConnectorTypes = CarProperty.Unknown,
                ),
            register = { manager, listener ->
                manager.carInfo.fetchEnergyProfile(executor, listener)
                null
            },
            mapper = { profile ->
                CarEnergyProfileData(
                    fuelTypes = profile.fuelTypes.toCarProperty { it.map(::toFuelType) },
                    evConnectorTypes =
                        profile.evConnectorTypes.toCarProperty { it.map(::toEvConnectorType) },
                )
            },
        )

    override fun observeEnergyLevel(): Flow<CarEnergyLevelData> =
        observeCarData(
            requiredPermission = permissions.energyLevel,
            onMissingPermission =
                CarEnergyLevelData(
                    batteryPercent = CarProperty.MissingPermission,
                    fuelPercent = CarProperty.MissingPermission,
                    energyIsLow = CarProperty.MissingPermission,
                    rangeRemainingMeters = CarProperty.MissingPermission,
                ),
            onNoData =
                CarEnergyLevelData(
                    batteryPercent = CarProperty.Unknown,
                    fuelPercent = CarProperty.Unknown,
                    energyIsLow = CarProperty.Unknown,
                    rangeRemainingMeters = CarProperty.Unknown,
                ),
            register = { manager, listener ->
                manager.carInfo.addEnergyLevelListener(executor, listener)
                return@observeCarData { manager.carInfo.removeEnergyLevelListener(listener) }
            },
            mapper = { energyLevel ->
                CarEnergyLevelData(
                    batteryPercent = energyLevel.batteryPercent.toCarProperty(),
                    fuelPercent = energyLevel.fuelPercent.toCarProperty(),
                    energyIsLow = energyLevel.energyIsLow.toCarProperty(),
                    rangeRemainingMeters = energyLevel.rangeRemainingMeters.toCarProperty(),
                    distanceUnit = toDistanceUnit(energyLevel.distanceDisplayUnit.value),
                )
            },
        )

    override fun observeSpeed(): Flow<CarSpeedData> =
        observeCarData(
            requiredPermission = permissions.speed,
            onMissingPermission =
                CarSpeedData(
                    displaySpeedMetersPerSecond = CarProperty.MissingPermission,
                    rawSpeedMetersPerSecond = CarProperty.MissingPermission,
                ),
            onNoData =
                CarSpeedData(
                    displaySpeedMetersPerSecond = CarProperty.Unknown,
                    rawSpeedMetersPerSecond = CarProperty.Unknown,
                ),
            register = { manager, listener ->
                manager.carInfo.addSpeedListener(executor, listener)
                return@observeCarData { manager.carInfo.removeSpeedListener(listener) }
            },
            mapper = { speed ->
                CarSpeedData(
                    displaySpeedMetersPerSecond = speed.displaySpeedMetersPerSecond.toCarProperty(),
                    rawSpeedMetersPerSecond = speed.rawSpeedMetersPerSecond.toCarProperty(),
                    speedUnit = toSpeedUnit(speed.speedDisplayUnit.value),
                )
            },
        )

    override fun observeMileage(): Flow<CarMileageData> =
        // Odometer requires system permission on AAOS which can't be granted to regular apps
        if (isAutomotive) {
            flowOf(CarMileageData(odometerMeters = CarProperty.Unavailable))
        } else {
            observeCarData(
                requiredPermission = permissions.mileage,
                onMissingPermission =
                    CarMileageData(odometerMeters = CarProperty.MissingPermission),
                onNoData = CarMileageData(odometerMeters = CarProperty.Unknown),
                register = { manager, listener ->
                    manager.carInfo.addMileageListener(executor, listener)
                    return@observeCarData { manager.carInfo.removeMileageListener(listener) }
                },
                mapper = { mileage ->
                    CarMileageData(
                        odometerMeters = mileage.odometerMeters.toCarProperty(),
                        distanceUnit = toDistanceUnit(mileage.distanceDisplayUnit.value),
                    )
                },
            )
        }

    override fun observeTollCard(): Flow<CarProperty<TollCardState>> =
        observeCarData<TollCard, CarProperty<TollCardState>>(
            requiredPermission = permissions.tollCard,
            onMissingPermission = CarProperty.MissingPermission,
            onNoData = CarProperty.Unknown,
            register = { manager, listener ->
                manager.carInfo.addTollListener(executor, listener)
                return@observeCarData { manager.carInfo.removeTollListener(listener) }
            },
            mapper = { tollCard -> tollCard.cardState.toCarProperty(::toTollCardState) },
        )

    override fun observeCompass(): Flow<CarProperty<List<Float>>> =
        observeCarData<Compass, CarProperty<List<Float>>>(
            requiredPermission = permissions.sensors,
            onMissingPermission = CarProperty.MissingPermission,
            onNoData = CarProperty.Unknown,
            register = { manager, listener ->
                manager.carSensors.addCompassListener(
                    CarSensors.UPDATE_RATE_NORMAL,
                    executor,
                    listener,
                )
                return@observeCarData { manager.carSensors.removeCompassListener(listener) }
            },
            mapper = { compass -> compass.orientations.toCarProperty() },
        )

    override fun observeAccelerometer(): Flow<CarProperty<List<Float>>> =
        observeCarData<Accelerometer, CarProperty<List<Float>>>(
            requiredPermission = permissions.sensors,
            onMissingPermission = CarProperty.MissingPermission,
            onNoData = CarProperty.Unknown,
            register = { manager, listener ->
                manager.carSensors.addAccelerometerListener(
                    CarSensors.UPDATE_RATE_NORMAL,
                    executor,
                    listener,
                )
                return@observeCarData { manager.carSensors.removeAccelerometerListener(listener) }
            },
            mapper = { accelerometer -> accelerometer.forces.toCarProperty() },
        )

    override fun observeGyroscope(): Flow<CarProperty<List<Float>>> =
        observeCarData<Gyroscope, CarProperty<List<Float>>>(
            requiredPermission = permissions.sensors,
            onMissingPermission = CarProperty.MissingPermission,
            onNoData = CarProperty.Unknown,
            register = { manager, listener ->
                manager.carSensors.addGyroscopeListener(
                    CarSensors.UPDATE_RATE_NORMAL,
                    executor,
                    listener,
                )
                return@observeCarData { manager.carSensors.removeGyroscopeListener(listener) }
            },
            mapper = { gyroscope -> gyroscope.rotations.toCarProperty() },
        )

    /**
     * Registers [register] listener on the hardware manager of the connected car and maps every
     * received value with [mapper]. [register] returns optional unregister callback which is called
     * when the flow is cancelled or the car session ends. Some hosts report values only when they
     * change, so [onNoData] is emitted when nothing arrives in time.
     */
    private fun <T : Any, R> observeCarData(
        requiredPermission: String?,
        onMissingPermission: R,
        onNoData: R,
        register: (CarHardwareManager, OnCarDataAvailableListener<T>) -> (() -> Unit)?,
        mapper: (T) -> R,
    ): Flow<R> =
        carContextHolder.carContext.flatMapLatest { carContext ->
            when {
                carContext == null -> emptyFlow()
                requiredPermission != null && !isPermissionGranted(requiredPermission) ->
                    flowOf(onMissingPermission)
                else ->
                    callbackFlow {
                            val listener = OnCarDataAvailableListener<T> { trySend(mapper(it)) }
                            val unregister =
                                try {
                                    register(
                                        carContext.getCarService(CarHardwareManager::class.java),
                                        listener,
                                    )
                                } catch (e: SecurityException) {
                                    Logger.w(e) { "Missing car permission" }
                                    trySend(onMissingPermission)
                                    null
                                } catch (e: IllegalStateException) {
                                    Logger.w(e) { "Car hardware not available" }
                                    null
                                }
                            awaitClose { runCatching { unregister?.invoke() } }
                        }
                        .withDefaultAfter(onNoData, NO_DATA_TIMEOUT_MS)
            }
        }

    /** Emits [default] when the upstream doesn't produce any value within [timeoutMillis]. */
    private fun <T> Flow<T>.withDefaultAfter(default: T, timeoutMillis: Long): Flow<T> =
        channelFlow {
            val defaultJob = launch {
                delay(timeoutMillis.milliseconds)
                send(default)
            }
            collect {
                defaultJob.cancel()
                send(it)
            }
        }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun <T> CarValue<T>.toCarProperty(): CarProperty<T> = toCarProperty { it }

    private fun <T, R> CarValue<T>.toCarProperty(mapper: (T) -> R): CarProperty<R> =
        when (status) {
            CarValue.STATUS_SUCCESS ->
                value?.let { CarProperty.Available(mapper(it)) } ?: CarProperty.Unknown
            CarValue.STATUS_UNIMPLEMENTED -> CarProperty.Unimplemented
            CarValue.STATUS_UNAVAILABLE -> CarProperty.Unavailable
            else -> CarProperty.Unknown
        }

    private fun toDistanceUnit(unit: Int?) =
        if (unit == CarUnit.MILE) DistanceUnit.MILE else DistanceUnit.KILOMETER

    private fun toSpeedUnit(unit: Int?) =
        when (unit) {
            CarUnit.METERS_PER_SEC -> SpeedUnit.METERS_PER_SECOND
            CarUnit.MILES_PER_HOUR -> SpeedUnit.MILES_PER_HOUR
            else -> SpeedUnit.KILOMETERS_PER_HOUR
        }

    private fun toTollCardState(state: Int) =
        when (state) {
            TollCard.TOLLCARD_STATE_VALID -> TollCardState.VALID
            TollCard.TOLLCARD_STATE_INVALID -> TollCardState.INVALID
            TollCard.TOLLCARD_STATE_NOT_INSERTED -> TollCardState.NOT_INSERTED
            else -> TollCardState.UNKNOWN
        }

    private fun toFuelType(type: Int) =
        when (type) {
            EnergyProfile.FUEL_TYPE_UNLEADED -> FuelType.UNLEADED
            EnergyProfile.FUEL_TYPE_LEADED -> FuelType.LEADED
            EnergyProfile.FUEL_TYPE_DIESEL_1 -> FuelType.DIESEL_1
            EnergyProfile.FUEL_TYPE_DIESEL_2 -> FuelType.DIESEL_2
            EnergyProfile.FUEL_TYPE_BIODIESEL -> FuelType.BIODIESEL
            EnergyProfile.FUEL_TYPE_E85 -> FuelType.E85
            EnergyProfile.FUEL_TYPE_LPG -> FuelType.LPG
            EnergyProfile.FUEL_TYPE_CNG -> FuelType.CNG
            EnergyProfile.FUEL_TYPE_LNG -> FuelType.LNG
            EnergyProfile.FUEL_TYPE_ELECTRIC -> FuelType.ELECTRIC
            EnergyProfile.FUEL_TYPE_HYDROGEN -> FuelType.HYDROGEN
            EnergyProfile.FUEL_TYPE_OTHER -> FuelType.OTHER
            else -> FuelType.UNKNOWN
        }

    private fun toEvConnectorType(type: Int) =
        when (type) {
            EnergyProfile.EVCONNECTOR_TYPE_J1772 -> EvConnectorType.J1772
            EnergyProfile.EVCONNECTOR_TYPE_MENNEKES -> EvConnectorType.MENNEKES
            EnergyProfile.EVCONNECTOR_TYPE_CHADEMO -> EvConnectorType.CHADEMO
            EnergyProfile.EVCONNECTOR_TYPE_COMBO_1 -> EvConnectorType.COMBO_1
            EnergyProfile.EVCONNECTOR_TYPE_COMBO_2 -> EvConnectorType.COMBO_2
            EnergyProfile.EVCONNECTOR_TYPE_TESLA_ROADSTER -> EvConnectorType.TESLA_ROADSTER
            EnergyProfile.EVCONNECTOR_TYPE_TESLA_HPWC -> EvConnectorType.TESLA_HPWC
            EnergyProfile.EVCONNECTOR_TYPE_TESLA_SUPERCHARGER -> EvConnectorType.TESLA_SUPERCHARGER
            EnergyProfile.EVCONNECTOR_TYPE_GBT -> EvConnectorType.GBT
            EnergyProfile.EVCONNECTOR_TYPE_GBT_DC -> EvConnectorType.GBT_DC
            EnergyProfile.EVCONNECTOR_TYPE_SCAME -> EvConnectorType.SCAME
            EnergyProfile.EVCONNECTOR_TYPE_OTHER -> EvConnectorType.OTHER
            else -> EvConnectorType.UNKNOWN
        }

    /** Car permissions differ between Android Auto and Android Automotive OS. */
    private class CarPermissions(
        val model: String?,
        val energyProfile: String?,
        val energyLevel: String?,
        val speed: String?,
        val mileage: String?,
        val tollCard: String?,
        val sensors: String?,
        // Requested together with the required ones, e.g. coarse location must accompany fine one
        val companions: List<String> = emptyList(),
    ) {
        val all =
            (listOfNotNull(model, energyProfile, energyLevel, speed, mileage, tollCard, sensors) +
                    companions)
                .distinct()

        companion object {
            val PROJECTED =
                CarPermissions(
                    model = null,
                    energyProfile = "com.google.android.gms.permission.CAR_FUEL",
                    energyLevel = "com.google.android.gms.permission.CAR_FUEL",
                    speed = "com.google.android.gms.permission.CAR_SPEED",
                    mileage = "com.google.android.gms.permission.CAR_MILEAGE",
                    tollCard = null,
                    // Android Auto host requires location to share car sensors with the app
                    sensors = Manifest.permission.ACCESS_FINE_LOCATION,
                    companions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                )

            val AUTOMOTIVE =
                CarPermissions(
                    model = "android.car.permission.CAR_INFO",
                    energyProfile = "android.car.permission.CAR_INFO",
                    energyLevel = "android.car.permission.CAR_ENERGY",
                    speed = "android.car.permission.CAR_SPEED",
                    mileage = null,
                    tollCard = "android.car.permission.CAR_INFO",
                    sensors = null,
                )
        }
    }

    companion object {
        private const val NO_DATA_TIMEOUT_MS = 3000L
    }
}
