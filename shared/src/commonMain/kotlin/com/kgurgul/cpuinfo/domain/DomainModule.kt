package com.kgurgul.cpuinfo.domain

import com.kgurgul.cpuinfo.domain.action.ExternalAppAction
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.observable.CpuDataObservable
import com.kgurgul.cpuinfo.domain.observable.GpuDataObservable
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.domain.observable.RamDataObservable
import com.kgurgul.cpuinfo.domain.observable.SensorsDataObservable
import com.kgurgul.cpuinfo.domain.observable.StorageDataObservable
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable
import com.kgurgul.cpuinfo.domain.result.GetHardwareDataInteractor
import com.kgurgul.cpuinfo.domain.result.GetOsDataInteractor
import com.kgurgul.cpuinfo.domain.result.GetPackageNameInteractor
import com.kgurgul.cpuinfo.domain.result.GetScreenDataInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::ExternalAppAction)
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
}
