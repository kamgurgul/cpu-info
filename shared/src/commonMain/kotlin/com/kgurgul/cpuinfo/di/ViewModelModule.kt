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
}