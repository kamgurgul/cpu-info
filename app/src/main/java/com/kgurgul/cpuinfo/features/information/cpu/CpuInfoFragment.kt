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

import android.arch.lifecycle.ViewModelProviders
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import javax.inject.Inject

/**
 * Displays information about device CPU taken form /proc/cpuinfo file
 *
 * @author kgurgul
 */
class CpuInfoFragment : BaseRvFragment() {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<CpuInfoViewModel>

    private val viewModel: CpuInfoViewModel by lazy {
        ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(CpuInfoViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startProvidingData()
    }

    override fun onStop() {
        viewModel.stopProvidingData()
        super.onStop()
    }

    override fun setupRecyclerViewAdapter() {
        recyclerView.addItemDecoration(DividerItemDecoration(context))

        val infoItemsAdapter = InfoItemsAdapter(context, viewModel.dataObservableList,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT)
        recyclerView.adapter = infoItemsAdapter
        lifecycle.addObserver(infoItemsAdapter)
    }
}