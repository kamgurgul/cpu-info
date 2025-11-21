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
package com.kgurgul.cpuinfo.di

import com.kgurgul.cpuinfo.features.HostViewModel
import com.kgurgul.cpuinfo.features.applications.ApplicationsViewModel
import com.kgurgul.cpuinfo.features.information.InfoContainerViewModel
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoViewModel
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoViewModel
import com.kgurgul.cpuinfo.features.information.os.OsInfoViewModel
import com.kgurgul.cpuinfo.features.information.ram.RamInfoViewModel
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoViewModel
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoViewModel
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoViewModel
import com.kgurgul.cpuinfo.features.processes.ProcessesViewModel
import com.kgurgul.cpuinfo.features.settings.SettingsViewModel
import com.kgurgul.cpuinfo.features.settings.licenses.LicensesViewModel
import com.kgurgul.cpuinfo.features.temperature.TemperatureViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::ApplicationsViewModel)
    viewModelOf(::CpuInfoViewModel)
    viewModelOf(::GpuInfoViewModel)
    viewModelOf(::HardwareInfoViewModel)
    viewModelOf(::HostViewModel)
    viewModelOf(::InfoContainerViewModel)
    viewModelOf(::OsInfoViewModel)
    viewModelOf(::ProcessesViewModel)
    viewModelOf(::RamInfoViewModel)
    viewModelOf(::SensorsInfoViewModel)
    viewModelOf(::ScreenInfoViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::StorageInfoViewModel)
    viewModelOf(::TemperatureViewModel)
    viewModelOf(::LicensesViewModel)
}
