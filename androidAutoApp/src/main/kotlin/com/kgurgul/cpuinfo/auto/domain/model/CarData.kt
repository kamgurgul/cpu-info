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
package com.kgurgul.cpuinfo.auto.domain.model

/** Value read from the car hardware together with its availability status. */
sealed interface CarProperty<out T> {
    data object Loading : CarProperty<Nothing>

    data object Unknown : CarProperty<Nothing>

    data object Unimplemented : CarProperty<Nothing>

    data object Unavailable : CarProperty<Nothing>

    data object MissingPermission : CarProperty<Nothing>

    data class Available<T>(val value: T) : CarProperty<T>
}

data class CarData(
    val host: CarHostData? = null,
    val model: CarModelData = CarModelData(),
    val energyProfile: CarEnergyProfileData = CarEnergyProfileData(),
    val energyLevel: CarEnergyLevelData = CarEnergyLevelData(),
    val speed: CarSpeedData = CarSpeedData(),
    val mileage: CarMileageData = CarMileageData(),
    val tollCard: CarProperty<TollCardState> = CarProperty.Loading,
    val compass: CarProperty<List<Float>> = CarProperty.Loading,
    val accelerometer: CarProperty<List<Float>> = CarProperty.Loading,
    val gyroscope: CarProperty<List<Float>> = CarProperty.Loading,
    val missingPermissions: List<String> = emptyList(),
)

data class CarHostData(val carAppApiLevel: Int, val hostPackageName: String?)

data class CarModelData(
    val manufacturer: CarProperty<String> = CarProperty.Loading,
    val name: CarProperty<String> = CarProperty.Loading,
    val year: CarProperty<Int> = CarProperty.Loading,
)

data class CarEnergyProfileData(
    val fuelTypes: CarProperty<List<FuelType>> = CarProperty.Loading,
    val evConnectorTypes: CarProperty<List<EvConnectorType>> = CarProperty.Loading,
)

data class CarEnergyLevelData(
    val batteryPercent: CarProperty<Float> = CarProperty.Loading,
    val fuelPercent: CarProperty<Float> = CarProperty.Loading,
    val energyIsLow: CarProperty<Boolean> = CarProperty.Loading,
    val rangeRemainingMeters: CarProperty<Float> = CarProperty.Loading,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETER,
)

data class CarSpeedData(
    val displaySpeedMetersPerSecond: CarProperty<Float> = CarProperty.Loading,
    val rawSpeedMetersPerSecond: CarProperty<Float> = CarProperty.Loading,
    val speedUnit: SpeedUnit = SpeedUnit.KILOMETERS_PER_HOUR,
)

data class CarMileageData(
    val odometerMeters: CarProperty<Float> = CarProperty.Loading,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETER,
)

enum class DistanceUnit {
    KILOMETER,
    MILE,
}

enum class SpeedUnit {
    METERS_PER_SECOND,
    KILOMETERS_PER_HOUR,
    MILES_PER_HOUR,
}

enum class TollCardState {
    UNKNOWN,
    VALID,
    INVALID,
    NOT_INSERTED,
}

enum class FuelType {
    UNKNOWN,
    UNLEADED,
    LEADED,
    DIESEL_1,
    DIESEL_2,
    BIODIESEL,
    E85,
    LPG,
    CNG,
    LNG,
    ELECTRIC,
    HYDROGEN,
    OTHER,
}

enum class EvConnectorType {
    UNKNOWN,
    J1772,
    MENNEKES,
    CHADEMO,
    COMBO_1,
    COMBO_2,
    TESLA_ROADSTER,
    TESLA_HPWC,
    TESLA_SUPERCHARGER,
    GBT,
    GBT_DC,
    SCAME,
    OTHER,
}
