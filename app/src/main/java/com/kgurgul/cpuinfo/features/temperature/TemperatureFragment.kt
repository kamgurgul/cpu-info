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

package com.kgurgul.cpuinfo.features.temperature

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentTemperatureBinding
import com.kgurgul.cpuinfo.features.information.base.BaseFragment
import com.kgurgul.cpuinfo.features.temperature.list.TemperatureAdapter
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Displays information about available temperatures
 *
 * @author kgurgul
 */
@AndroidEntryPoint
class TemperatureFragment : BaseFragment<FragmentTemperatureBinding>(
        R.layout.fragment_temperature
) {

    private val viewModel: TemperatureViewModel by viewModels()

    @Inject
    lateinit var temperatureFormatter: TemperatureFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecycleView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startTemperatureRefreshing()
    }

    override fun onStop() {
        viewModel.stopTemperatureRefreshing()
        super.onStop()
    }

    private fun setupRecycleView() {
        val temperatureAdapter = TemperatureAdapter(temperatureFormatter,
                viewModel.temperatureListLiveData)
        viewModel.temperatureListLiveData.listStatusChangeNotificator.observe(viewLifecycleOwner,
                ListLiveDataObserver(temperatureAdapter))
        binding.apply {
            tempRv.layoutManager = LinearLayoutManager(context)
            tempRv.adapter = temperatureAdapter
            (tempRv.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }
}
