/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.di.modules

import com.kgurgul.cpuinfo.di.FragmentScope
import com.kgurgul.cpuinfo.features.applications.ApplicationsFragment
import com.kgurgul.cpuinfo.features.information.InfoContainerFragment
import com.kgurgul.cpuinfo.features.information.android.AndroidInfoFragment
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoFragment
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoFragment
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoFragment
import com.kgurgul.cpuinfo.features.information.ram.RamInfoFragment
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoFragment
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoFragment
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoFragment
import com.kgurgul.cpuinfo.features.processes.ProcessesFragment
import com.kgurgul.cpuinfo.features.temperature.TemperatureFragment
import com.kgurgul.cpuinfo.features.temperature.TemperatureModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Injector for all fragments and their modules
 *
 * @author kgurgul
 */
@Module
abstract class FragmentBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeAndroidInfoFragment(): AndroidInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeCpuInfoFragment(): CpuInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoContainerFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeBaseInfoFragment(): BaseRvFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeGpuInfoFragment(): GpuInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeRamInfoFragment(): RamInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeSensorsInfoFragment(): SensorsInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeStorageInfoFragment(): StorageInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeScreenInfoFragment(): ScreenInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeHardwareInfoFragment(): HardwareInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeApplicationsFragment(): ApplicationsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeProcessesFragment(): ProcessesFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TemperatureModule::class])
    abstract fun contributeTemperatureFragment(): TemperatureFragment
}