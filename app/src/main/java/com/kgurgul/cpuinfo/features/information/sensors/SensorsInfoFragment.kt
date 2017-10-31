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

package com.kgurgul.cpuinfo.features.information.sensors

import android.arch.lifecycle.ViewModelProviders
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import javax.inject.Inject

/**
 * Displays all data from device sensors
 *
 * @author kgurgul
 */
class SensorsInfoFragment : BaseRvFragment() {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<SensorsInfoViewModel>

    private val viewModel: SensorsInfoViewModel by lazy {
        ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(SensorsInfoViewModel::class.java)
    }

    private val infoItemsAdapter: InfoItemsAdapter by lazy {
        InfoItemsAdapter(context, viewModel.dataObservableList,
                InfoItemsAdapter.LayoutType.VERTICAL_LAYOUT)
    }

    override fun onStart() {
        super.onStart()
        infoItemsAdapter.registerListChangeNotifier()
        viewModel.startProvidingData()
    }

    override fun onStop() {
        viewModel.stopProvidingData()
        infoItemsAdapter.unregisterListChangeNotifier()
        super.onStop()
    }

    override fun setupRecyclerViewAdapter() {
        recyclerView.addItemDecoration(DividerItemDecoration(context))
        recyclerView.adapter = infoItemsAdapter
    }
}