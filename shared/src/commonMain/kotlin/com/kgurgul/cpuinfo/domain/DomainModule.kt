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
package com.kgurgul.cpuinfo.domain

import com.kgurgul.cpuinfo.domain.action.ExternalAppAction
import com.kgurgul.cpuinfo.domain.action.IExternalAppAction
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.observable.CpuDataObservable
import com.kgurgul.cpuinfo.domain.observable.GetOsDataInteractor
import com.kgurgul.cpuinfo.domain.observable.GpuDataObservable
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.domain.observable.RamDataObservable
import com.kgurgul.cpuinfo.domain.observable.SensorsDataObservable
import com.kgurgul.cpuinfo.domain.observable.StorageDataObservable
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable
import com.kgurgul.cpuinfo.domain.result.FilterApplicationsInteractor
import com.kgurgul.cpuinfo.domain.result.FilterProcessesInteractor
import com.kgurgul.cpuinfo.domain.result.GetHardwareDataInteractor
import com.kgurgul.cpuinfo.domain.result.GetLicensesInteractor
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.domain.result.GetScreenDataInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::ExternalAppAction) bind IExternalAppAction::class
    factoryOf(::RamCleanupAction)
    factoryOf(::ApplicationsDataObservable)
    factoryOf(::CpuDataObservable)
    factoryOf(::GpuDataObservable)
    factoryOf(::ProcessesDataObservable)
    factoryOf(::RamDataObservable)
    factoryOf(::SensorsDataObservable)
    factoryOf(::StorageDataObservable)
    factoryOf(::TemperatureDataObservable)
    factoryOf(::GetHardwareDataInteractor)
    factoryOf(::GetOsDataInteractor)
    factoryOf(::GetPackageNameInteractor)
    factoryOf(::GetScreenDataInteractor)
    factoryOf(::GetLicensesInteractor)
    factoryOf(::FilterApplicationsInteractor)
    factoryOf(::FilterProcessesInteractor)
}
