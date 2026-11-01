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
package com.kgurgul.cpuinfo.auto.ui

import com.kgurgul.cpuinfo.auto.domain.model.CarData
import com.kgurgul.cpuinfo.auto.domain.model.CarProperty
import com.kgurgul.cpuinfo.auto.domain.model.DistanceUnit
import com.kgurgul.cpuinfo.auto.domain.model.EvConnectorType
import com.kgurgul.cpuinfo.auto.domain.model.FuelType
import com.kgurgul.cpuinfo.auto.domain.model.SpeedUnit
import com.kgurgul.cpuinfo.auto.domain.model.TollCardState
import com.kgurgul.cpuinfo.auto.features.CarInfoCategory
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.car_accelerometer
import com.kgurgul.cpuinfo.shared.car_app_api_level
import com.kgurgul.cpuinfo.shared.car_battery_level
import com.kgurgul.cpuinfo.shared.car_compass
import com.kgurgul.cpuinfo.shared.car_connector_chademo
import com.kgurgul.cpuinfo.shared.car_connector_combo_1
import com.kgurgul.cpuinfo.shared.car_connector_combo_2
import com.kgurgul.cpuinfo.shared.car_connector_gbt
import com.kgurgul.cpuinfo.shared.car_connector_gbt_dc
import com.kgurgul.cpuinfo.shared.car_connector_j1772
import com.kgurgul.cpuinfo.shared.car_connector_mennekes
import com.kgurgul.cpuinfo.shared.car_connector_scame
import com.kgurgul.cpuinfo.shared.car_connector_tesla_hpwc
import com.kgurgul.cpuinfo.shared.car_connector_tesla_roadster
import com.kgurgul.cpuinfo.shared.car_connector_tesla_supercharger
import com.kgurgul.cpuinfo.shared.car_energy_low
import com.kgurgul.cpuinfo.shared.car_ev_connectors
import com.kgurgul.cpuinfo.shared.car_fuel_biodiesel
import com.kgurgul.cpuinfo.shared.car_fuel_cng
import com.kgurgul.cpuinfo.shared.car_fuel_diesel_1
import com.kgurgul.cpuinfo.shared.car_fuel_diesel_2
import com.kgurgul.cpuinfo.shared.car_fuel_e85
import com.kgurgul.cpuinfo.shared.car_fuel_electric
import com.kgurgul.cpuinfo.shared.car_fuel_hydrogen
import com.kgurgul.cpuinfo.shared.car_fuel_leaded
import com.kgurgul.cpuinfo.shared.car_fuel_level
import com.kgurgul.cpuinfo.shared.car_fuel_lng
import com.kgurgul.cpuinfo.shared.car_fuel_lpg
import com.kgurgul.cpuinfo.shared.car_fuel_types
import com.kgurgul.cpuinfo.shared.car_fuel_unleaded
import com.kgurgul.cpuinfo.shared.car_gyroscope
import com.kgurgul.cpuinfo.shared.car_host
import com.kgurgul.cpuinfo.shared.car_manufacturer
import com.kgurgul.cpuinfo.shared.car_model
import com.kgurgul.cpuinfo.shared.car_model_year
import com.kgurgul.cpuinfo.shared.car_odometer
import com.kgurgul.cpuinfo.shared.car_other
import com.kgurgul.cpuinfo.shared.car_range_remaining
import com.kgurgul.cpuinfo.shared.car_raw_speed
import com.kgurgul.cpuinfo.shared.car_speed
import com.kgurgul.cpuinfo.shared.car_toll_card
import com.kgurgul.cpuinfo.shared.car_toll_card_invalid
import com.kgurgul.cpuinfo.shared.car_toll_card_not_inserted
import com.kgurgul.cpuinfo.shared.car_toll_card_valid
import com.kgurgul.cpuinfo.shared.car_unit_degrees
import com.kgurgul.cpuinfo.shared.car_unit_km
import com.kgurgul.cpuinfo.shared.car_unit_km_h
import com.kgurgul.cpuinfo.shared.car_unit_m_s
import com.kgurgul.cpuinfo.shared.car_unit_m_s2
import com.kgurgul.cpuinfo.shared.car_unit_mi
import com.kgurgul.cpuinfo.shared.car_unit_mph
import com.kgurgul.cpuinfo.shared.car_unit_percent
import com.kgurgul.cpuinfo.shared.car_unit_rad_s
import com.kgurgul.cpuinfo.shared.car_value_loading
import com.kgurgul.cpuinfo.shared.car_value_missing_permission
import com.kgurgul.cpuinfo.shared.car_value_unavailable
import com.kgurgul.cpuinfo.shared.car_value_unimplemented
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.yes
import java.util.Locale
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

data class CarInfoItem(val title: String, val value: String)

/** Converts [CarData] into title-value pairs which can be displayed in car templates. */
object CarInfoItemsMapper {

    suspend fun map(carData: CarData, category: CarInfoCategory): List<CarInfoItem> =
        when (category) {
            CarInfoCategory.VEHICLE -> mapVehicle(carData)
            CarInfoCategory.ENERGY -> mapEnergy(carData)
            CarInfoCategory.DRIVING -> mapDriving(carData)
            CarInfoCategory.SENSORS -> mapSensors(carData)
            CarInfoCategory.HOST -> mapHost(carData)
        }

    private suspend fun mapVehicle(carData: CarData) =
        with(carData) {
            listOf(
                item(Res.string.car_manufacturer, model.manufacturer) { it },
                item(Res.string.car_model, model.name) { it },
                item(Res.string.car_model_year, model.year) { it.toString() },
                item(Res.string.car_fuel_types, energyProfile.fuelTypes) { fuelTypes ->
                    joinOrUnknown(fuelTypes.map { getString(it.toStringResource()) })
                },
                item(Res.string.car_ev_connectors, energyProfile.evConnectorTypes) { connectors ->
                    joinOrUnknown(connectors.map { getString(it.toStringResource()) })
                },
            )
        }

    private suspend fun mapEnergy(carData: CarData) =
        with(carData.energyLevel) {
            listOf(
                item(Res.string.car_battery_level, batteryPercent) {
                    getString(Res.string.car_unit_percent, it.format(decimals = 0))
                },
                item(Res.string.car_fuel_level, fuelPercent) {
                    getString(Res.string.car_unit_percent, it.format(decimals = 0))
                },
                item(Res.string.car_range_remaining, rangeRemainingMeters) {
                    formatDistance(it, distanceUnit)
                },
                item(Res.string.car_energy_low, energyIsLow) {
                    getString(if (it) Res.string.yes else Res.string.no)
                },
            )
        }

    private suspend fun mapDriving(carData: CarData) =
        with(carData) {
            listOf(
                item(Res.string.car_speed, speed.displaySpeedMetersPerSecond) {
                    formatSpeed(it, speed.speedUnit)
                },
                item(Res.string.car_raw_speed, speed.rawSpeedMetersPerSecond) {
                    formatSpeed(it, speed.speedUnit)
                },
                item(Res.string.car_odometer, mileage.odometerMeters) {
                    formatDistance(it, mileage.distanceUnit)
                },
                item(Res.string.car_toll_card, tollCard) { getString(it.toStringResource()) },
            )
        }

    private suspend fun mapSensors(carData: CarData) =
        with(carData) {
            listOf(
                item(Res.string.car_compass, compass) {
                    formatVector(it, Res.string.car_unit_degrees)
                },
                item(Res.string.car_accelerometer, accelerometer) {
                    formatVector(it, Res.string.car_unit_m_s2)
                },
                item(Res.string.car_gyroscope, gyroscope) {
                    formatVector(it, Res.string.car_unit_rad_s)
                },
            )
        }

    private suspend fun mapHost(carData: CarData): List<CarInfoItem> {
        val host = carData.host
        val unavailable = getString(Res.string.car_value_unavailable)
        return listOf(
            CarInfoItem(
                title = getString(Res.string.car_app_api_level),
                value = host?.carAppApiLevel?.toString() ?: unavailable,
            ),
            CarInfoItem(
                title = getString(Res.string.car_host),
                value = host?.hostPackageName ?: unavailable,
            ),
        )
    }

    private suspend fun <T> item(
        title: StringResource,
        property: CarProperty<T>,
        formatter: suspend (T) -> String,
    ) = CarInfoItem(title = getString(title), value = property.format(formatter))

    private suspend fun <T> CarProperty<T>.format(formatter: suspend (T) -> String): String =
        when (this) {
            is CarProperty.Available -> formatter(value)
            CarProperty.Loading -> getString(Res.string.car_value_loading)
            CarProperty.MissingPermission -> getString(Res.string.car_value_missing_permission)
            CarProperty.Unavailable -> getString(Res.string.car_value_unavailable)
            CarProperty.Unimplemented -> getString(Res.string.car_value_unimplemented)
            CarProperty.Unknown -> getString(Res.string.unknown)
        }

    private suspend fun joinOrUnknown(values: List<String>) =
        values.joinToString().ifEmpty { getString(Res.string.unknown) }

    private suspend fun formatDistance(meters: Float, unit: DistanceUnit) =
        when (unit) {
            DistanceUnit.KILOMETER ->
                getString(Res.string.car_unit_km, (meters / METERS_IN_KILOMETER).format())
            DistanceUnit.MILE ->
                getString(Res.string.car_unit_mi, (meters / METERS_IN_MILE).format())
        }

    private suspend fun formatSpeed(metersPerSecond: Float, unit: SpeedUnit) =
        when (unit) {
            SpeedUnit.METERS_PER_SECOND ->
                getString(Res.string.car_unit_m_s, metersPerSecond.format())
            SpeedUnit.KILOMETERS_PER_HOUR ->
                getString(Res.string.car_unit_km_h, (metersPerSecond * MPS_TO_KMH).format())
            SpeedUnit.MILES_PER_HOUR ->
                getString(Res.string.car_unit_mph, (metersPerSecond * MPS_TO_MPH).format())
        }

    private suspend fun formatVector(values: List<Float>, unit: StringResource) =
        joinOrUnknown(values.map { getString(unit, it.format(decimals = 2)) })

    private fun Float.format(decimals: Int = 1) =
        String.format(Locale.getDefault(), "%.${decimals}f", this)

    private fun FuelType.toStringResource() =
        when (this) {
            FuelType.UNLEADED -> Res.string.car_fuel_unleaded
            FuelType.LEADED -> Res.string.car_fuel_leaded
            FuelType.DIESEL_1 -> Res.string.car_fuel_diesel_1
            FuelType.DIESEL_2 -> Res.string.car_fuel_diesel_2
            FuelType.BIODIESEL -> Res.string.car_fuel_biodiesel
            FuelType.E85 -> Res.string.car_fuel_e85
            FuelType.LPG -> Res.string.car_fuel_lpg
            FuelType.CNG -> Res.string.car_fuel_cng
            FuelType.LNG -> Res.string.car_fuel_lng
            FuelType.ELECTRIC -> Res.string.car_fuel_electric
            FuelType.HYDROGEN -> Res.string.car_fuel_hydrogen
            FuelType.OTHER -> Res.string.car_other
            FuelType.UNKNOWN -> Res.string.unknown
        }

    private fun EvConnectorType.toStringResource() =
        when (this) {
            EvConnectorType.J1772 -> Res.string.car_connector_j1772
            EvConnectorType.MENNEKES -> Res.string.car_connector_mennekes
            EvConnectorType.CHADEMO -> Res.string.car_connector_chademo
            EvConnectorType.COMBO_1 -> Res.string.car_connector_combo_1
            EvConnectorType.COMBO_2 -> Res.string.car_connector_combo_2
            EvConnectorType.TESLA_ROADSTER -> Res.string.car_connector_tesla_roadster
            EvConnectorType.TESLA_HPWC -> Res.string.car_connector_tesla_hpwc
            EvConnectorType.TESLA_SUPERCHARGER -> Res.string.car_connector_tesla_supercharger
            EvConnectorType.GBT -> Res.string.car_connector_gbt
            EvConnectorType.GBT_DC -> Res.string.car_connector_gbt_dc
            EvConnectorType.SCAME -> Res.string.car_connector_scame
            EvConnectorType.OTHER -> Res.string.car_other
            EvConnectorType.UNKNOWN -> Res.string.unknown
        }

    private fun TollCardState.toStringResource() =
        when (this) {
            TollCardState.VALID -> Res.string.car_toll_card_valid
            TollCardState.INVALID -> Res.string.car_toll_card_invalid
            TollCardState.NOT_INSERTED -> Res.string.car_toll_card_not_inserted
            TollCardState.UNKNOWN -> Res.string.unknown
        }

    private const val METERS_IN_KILOMETER = 1000f
    private const val METERS_IN_MILE = 1609.344f
    private const val MPS_TO_KMH = 3.6f
    private const val MPS_TO_MPH = 2.236936f
}
