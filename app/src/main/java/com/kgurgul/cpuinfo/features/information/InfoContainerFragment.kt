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

package com.kgurgul.cpuinfo.features.information

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentInfoBinding
import com.kgurgul.cpuinfo.features.information.base.BaseFragment
import com.kgurgul.cpuinfo.features.information.base.ViewStateAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment which is base for all hardware and software information fragments
 *
 * @author kgurgul
 */
@AndroidEntryPoint
class InfoContainerFragment : BaseFragment<FragmentInfoBinding>(R.layout.fragment_info) {

    private val tabTitles = listOf(
            R.string.cpu,
            R.string.gpu,
            R.string.ram,
            R.string.storage,
            R.string.screen,
            R.string.android,
            R.string.hardware,
            R.string.sensors)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vpAdapter = ViewStateAdapter(this)
        binding.viewPager.adapter = vpAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = resources.getText(tabTitles[position])
        }.attach()
    }
}
