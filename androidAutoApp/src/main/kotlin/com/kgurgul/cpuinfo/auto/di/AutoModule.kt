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
package com.kgurgul.cpuinfo.auto.di

import com.kgurgul.cpuinfo.auto.data.CarContextHolder
import com.kgurgul.cpuinfo.auto.data.provider.CarDataProvider
import com.kgurgul.cpuinfo.auto.data.provider.ICarDataProvider
import com.kgurgul.cpuinfo.auto.domain.observable.CarDataObservable
import com.kgurgul.cpuinfo.auto.features.CarInfoViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val autoModule = module {
    singleOf(::CarContextHolder)
    factoryOf(::CarDataProvider) bind ICarDataProvider::class
    factoryOf(::CarDataObservable)
    viewModelOf(::CarInfoViewModel)
}
