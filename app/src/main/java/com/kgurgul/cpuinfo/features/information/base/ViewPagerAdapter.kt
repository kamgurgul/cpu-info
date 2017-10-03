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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
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
        FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CpuInfoFragment()
            1 -> GpuInfoFragment()
            2 -> RamInfoFragment()
            3 -> StorageInfoFragment()
            4 -> ScreenInfoFragment()
            5 -> AndroidInfoFragment()
            6 -> HardwareInfoFragment()
            7 -> SensorsInfoFragment()
            else -> throw IllegalArgumentException("Unknown position for ViewPager")
        }
    }

    override fun getCount(): Int {
        return 8
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.cpu)
            1 -> context.getString(R.string.gpu)
            2 -> context.getString(R.string.ram)
            3 -> context.getString(R.string.storage)
            4 -> context.getString(R.string.screen)
            5 -> context.getString(R.string.android)
            6 -> context.getString(R.string.hardware)
            7 -> context.getString(R.string.sensors)
            else -> throw IllegalArgumentException("Unknown position for ViewPager")
        }
    }
}
