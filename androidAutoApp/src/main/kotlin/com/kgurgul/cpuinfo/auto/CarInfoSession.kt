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
package com.kgurgul.cpuinfo.auto

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kgurgul.cpuinfo.auto.data.CarContextHolder
import com.kgurgul.cpuinfo.auto.features.CarInfoViewModel
import com.kgurgul.cpuinfo.auto.ui.CarHomeScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * Single car session. It owns [ViewModelStore] so all screens of the session share the same
 * [CarInfoViewModel] which is cleared when the car disconnects.
 */
class CarInfoSession : Session(), ViewModelStoreOwner, KoinComponent {

    private val carContextHolder: CarContextHolder by inject()

    override val viewModelStore = ViewModelStore()

    init {
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    carContextHolder.detach(carContext)
                    viewModelStore.clear()
                }
            }
        )
    }

    override fun onCreateScreen(intent: Intent): Screen {
        carContextHolder.attach(carContext)
        val viewModel =
            ViewModelProvider.create(
                    owner = this,
                    factory = viewModelFactory { initializer { get<CarInfoViewModel>() } },
                )[CarInfoViewModel::class]
        return CarHomeScreen(carContext = carContext, viewModel = viewModel)
    }
}
