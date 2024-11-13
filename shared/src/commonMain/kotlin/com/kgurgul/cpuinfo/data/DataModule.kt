package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.data.local.IDataStore
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.data.local.LocalDataStore
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.data.provider.ApplicationsDataProvider
import com.kgurgul.cpuinfo.data.provider.CpuDataNativeProvider
import com.kgurgul.cpuinfo.data.provider.CpuDataProvider
import com.kgurgul.cpuinfo.data.provider.GpuDataProvider
import com.kgurgul.cpuinfo.data.provider.HardwareDataProvider
import com.kgurgul.cpuinfo.data.provider.IApplicationsDataProvider
import com.kgurgul.cpuinfo.data.provider.IGpuDataProvider
import com.kgurgul.cpuinfo.data.provider.IOsDataProvider
import com.kgurgul.cpuinfo.data.provider.IProcessesProvider
import com.kgurgul.cpuinfo.data.provider.IStorageDataProvider
import com.kgurgul.cpuinfo.data.provider.ITemperatureProvider
import com.kgurgul.cpuinfo.data.provider.OsDataProvider
import com.kgurgul.cpuinfo.data.provider.PackageNameProvider
import com.kgurgul.cpuinfo.data.provider.ProcessesProvider
import com.kgurgul.cpuinfo.data.provider.RamDataProvider
import com.kgurgul.cpuinfo.data.provider.ScreenDataProvider
import com.kgurgul.cpuinfo.data.provider.SensorsInfoProvider
import com.kgurgul.cpuinfo.data.provider.StorageDataProvider
import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    factoryOf(::CpuDataProvider)
    factoryOf(::GpuDataProvider) bind IGpuDataProvider::class
    factoryOf(::HardwareDataProvider)
    factoryOf(::OsDataProvider) bind IOsDataProvider::class
    factoryOf(::PackageNameProvider)
    factoryOf(::ProcessesProvider) bind IProcessesProvider::class
    factoryOf(::RamDataProvider)
    factoryOf(::ScreenDataProvider)
    factoryOf(::SensorsInfoProvider)
    factoryOf(::StorageDataProvider) bind IStorageDataProvider::class
    factoryOf(::TemperatureProvider) bind ITemperatureProvider::class

    singleOf(::ApplicationsDataProvider) bind IApplicationsDataProvider::class
    singleOf(::CpuDataNativeProvider)
    singleOf(::LocalDataStore) bind IDataStore::class
    singleOf(::UserPreferencesRepository) bind IUserPreferencesRepository::class
}
