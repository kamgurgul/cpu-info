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

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentInfoBinding
import com.kgurgul.cpuinfo.di.Injectable
import com.kgurgul.cpuinfo.features.information.base.ViewPagerAdapter
import com.kgurgul.cpuinfo.utils.AutoClearedValue

/**
 * Fragment which is base for all hardware and software information fragments
 *
 * @author kgurgul
 */
class ContainerInfoFragment : Fragment(), Injectable {

    private lateinit var binding: AutoClearedValue<FragmentInfoBinding>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = AutoClearedValue(this,
                DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false))
        return binding.get().root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewPagerAdapter = ViewPagerAdapter(context, childFragmentManager)
        binding.get().viewPager.adapter = viewPagerAdapter
        binding.get().tabs.setupWithViewPager(binding.get().viewPager)
    }
}
