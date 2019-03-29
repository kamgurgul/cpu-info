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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentTemperatureBinding
import com.kgurgul.cpuinfo.di.Injectable
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.temperature.list.TemperatureAdapter
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import com.kgurgul.cpuinfo.utils.viewModelProvider
import javax.inject.Inject

/**
 * Displays information about available temperatures. Remove activity wrappers when Lifecycle
 * components will be integrated with support library.
 *
 * @author kgurgul
 */
class TemperatureFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<TemperatureViewModel>

    @Inject
    lateinit var temperatureFormatter: TemperatureFormatter

    private lateinit var viewModel: TemperatureViewModel
    private lateinit var binding: FragmentTemperatureBinding
    private lateinit var temperatureAdapter: TemperatureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelProvider(viewModelInjectionFactory)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_temperature, container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecycleView()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.startTemperatureRefreshing()
    }

    override fun onStop() {
        viewModel.stopTemperatureRefreshing()
        super.onStop()
    }

    /**
     * Set all necessary data for [android.support.v7.widget.RecyclerView]
     */
    private fun setupRecycleView() {
        temperatureAdapter = TemperatureAdapter(temperatureFormatter,
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
