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

import androidx.annotation.DrawableRes
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Header
import androidx.car.app.model.ListTemplate
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Car screen counterpart of compose `ObserveAsEvents`. */
fun <T> Screen.observeAsEvents(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    onEvent: suspend (T) -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            withContext(Dispatchers.Main.immediate) { flow.collect(onEvent) }
        }
    }
}

/** Collects [flow] while the screen is visible and refreshes the template on every new value. */
fun <T> Screen.collectAndInvalidate(flow: Flow<T>, onEach: (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                onEach(it)
                invalidate()
            }
        }
    }
}

/** [Header] is supported from Car API 7, older hosts need deprecated title and header action. */
@Suppress("DEPRECATION")
fun ListTemplate.Builder.setHeaderCompat(
    carContext: CarContext,
    title: String?,
    startAction: Action,
): ListTemplate.Builder =
    if (carContext.carAppApiLevel >= CAR_API_LEVEL_HEADER) {
        setHeader(
            Header.Builder()
                .setStartHeaderAction(startAction)
                .apply { title?.let { setTitle(it) } }
                .build()
        )
    } else {
        setHeaderAction(startAction).apply { title?.let { setTitle(it) } }
    }

fun CarContext.listContentLimit(): Int =
    getCarService(ConstraintManager::class.java)
        .getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_LIST)

fun CarContext.carIcon(@DrawableRes icon: Int): CarIcon =
    CarIcon.Builder(IconCompat.createWithResource(this, icon)).setTint(CarColor.DEFAULT).build()

private const val CAR_API_LEVEL_HEADER = 7
