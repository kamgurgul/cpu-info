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

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
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
class ViewPagerAdapter(val context: Context, manager: FragmentManager) :
        FragmentStatePagerAdapter(manager) {

    companion object {
        const val CPU_POS = 0
        const val GPU_POS = 1
        const val RAM_POS = 2
        const val STORAGE_POS = 3
        const val SCREEN_POS = 4
        const val ANDROID_POS = 5
        const val HARDWARE_POS = 6
        const val SENSORS_POS = 7

        const val INFO_PAGE_AMOUNT = 8
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment =
            when (position) {
                CPU_POS -> CpuInfoFragment()
                GPU_POS -> GpuInfoFragment()
                RAM_POS -> RamInfoFragment()
                STORAGE_POS -> StorageInfoFragment()
                SCREEN_POS -> ScreenInfoFragment()
                ANDROID_POS -> AndroidInfoFragment()
                HARDWARE_POS -> HardwareInfoFragment()
                SENSORS_POS -> SensorsInfoFragment()
                else -> throw IllegalArgumentException("Unknown position for ViewPager")
            }

    override fun getCount(): Int = INFO_PAGE_AMOUNT

    override fun getPageTitle(position: Int): CharSequence =
            when (position) {
                CPU_POS -> context.getString(R.string.cpu)
                GPU_POS -> context.getString(R.string.gpu)
                RAM_POS -> context.getString(R.string.ram)
                STORAGE_POS -> context.getString(R.string.storage)
                SCREEN_POS -> context.getString(R.string.screen)
                ANDROID_POS -> context.getString(R.string.android)
                HARDWARE_POS -> context.getString(R.string.hardware)
                SENSORS_POS -> context.getString(R.string.sensors)
                else -> throw IllegalArgumentException("Unknown position for ViewPager")
            }
}
