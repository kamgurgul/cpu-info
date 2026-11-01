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
package com.kgurgul.cpuinfo.auto.data

import androidx.car.app.CarContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Keeps [CarContext] of the currently connected car session. Car hardware can be accessed only
 * through the session context, so data providers observe it and stop when the car disconnects.
 */
class CarContextHolder {

    private val _carContext = MutableStateFlow<CarContext?>(null)
    val carContext: StateFlow<CarContext?> = _carContext.asStateFlow()

    fun attach(carContext: CarContext) {
        _carContext.value = carContext
    }

    fun detach(carContext: CarContext) {
        _carContext.compareAndSet(carContext, null)
    }
}
