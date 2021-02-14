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

package com.kgurgul.cpuinfo.features.information.base

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.information.android.AndroidInfoFragment
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoFragment
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoFragment
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoFragment
import com.kgurgul.cpuinfo.features.information.ram.RamInfoFragment
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoFragment
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoFragment
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoFragment

/**
 * Simple view pager for info fragments
 */
class InfoContainerStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment =
            when (position) {
                CPU_POS -> CpuInfoFragment()
                GPU_POS -> GpuInfoFragment()
                RAM_POS -> RamInfoFragment()
                STORAGE_POS -> StorageInfoFragment()
                SCREEN_POS -> ScreenInfoFragment()
                ANDROID_POS -> AndroidInfoFragment()
                HARDWARE_POS -> HardwareInfoFragment()
                SENSORS_POS -> SensorsInfoFragment()
                else -> throw IllegalArgumentException("Unknown position for ViewPager2")
            }

    override fun getItemCount(): Int = INFO_PAGE_AMOUNT

    fun getTitleRes(position: Int) = when (position) {
        CPU_POS -> R.string.cpu
        GPU_POS -> R.string.gpu
        RAM_POS -> R.string.ram
        STORAGE_POS -> R.string.storage
        SCREEN_POS -> R.string.screen
        ANDROID_POS -> R.string.android
        HARDWARE_POS -> R.string.hardware
        SENSORS_POS -> R.string.sensors
        else -> throw IllegalArgumentException("Unknown position for ViewPager2")
    }

    companion object {
        private const val CPU_POS = 0
        private const val GPU_POS = 1
        private const val RAM_POS = 2
        private const val STORAGE_POS = 3
        private const val SCREEN_POS = 4
        private const val ANDROID_POS = 5
        private const val HARDWARE_POS = 6
        private const val SENSORS_POS = 7

        private const val INFO_PAGE_AMOUNT = 8
    }
}
