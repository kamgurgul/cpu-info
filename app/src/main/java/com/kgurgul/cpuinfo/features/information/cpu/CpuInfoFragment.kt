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

package com.kgurgul.cpuinfo.features.information.cpu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentCpuInfoBinding
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseFragment
import javax.inject.Inject

/**
 * Displays information about device CPU taken form /proc/cpuinfo file
 *
 * @author kgurgul
 */
class CpuInfoFragment : BaseFragment<FragmentCpuInfoBinding>(R.layout.fragment_cpu_info) {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<CpuInfoViewModel>
    private val viewModel: CpuInfoViewModel by viewModels { viewModelInjectionFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = CpuInfoEpoxyController(requireContext())
        binding.recyclerView.adapter = controller.adapter
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            controller.setData(it)
        })
    }
}