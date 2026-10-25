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

import androidx.annotation.DrawableRes
import com.kgurgul.cpuinfo.auto.R
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.car_category_driving
import com.kgurgul.cpuinfo.shared.car_category_energy
import com.kgurgul.cpuinfo.shared.car_category_host
import com.kgurgul.cpuinfo.shared.car_category_vehicle
import com.kgurgul.cpuinfo.shared.sensors
import org.jetbrains.compose.resources.StringResource

enum class CarInfoCategory(val title: StringResource, @param:DrawableRes val icon: Int) {
    VEHICLE(Res.string.car_category_vehicle, R.drawable.ic_car_vehicle),
    ENERGY(Res.string.car_category_energy, R.drawable.ic_car_energy),
    DRIVING(Res.string.car_category_driving, R.drawable.ic_car_driving),
    SENSORS(Res.string.sensors, R.drawable.ic_car_sensors),
    HOST(Res.string.car_category_host, R.drawable.ic_car_host),
}
