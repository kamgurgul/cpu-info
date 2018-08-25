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

package com.kgurgul.cpuinfo.features.information.screen

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import com.kgurgul.cpuinfo.utils.DividerItemDecoration
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import javax.inject.Inject

/**
 * Displays information about device screen resolution, density etc.
 *
 * @author kgurgul
 */
class ScreenInfoFragment : BaseRvFragment() {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<ScreenInfoViewModel>

    private lateinit var viewModel: ScreenInfoViewModel
    private lateinit var infoItemsAdapter: InfoItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelInjectionFactory)
                .get(ScreenInfoViewModel::class.java)
    }

    override fun setupRecyclerViewAdapter() {
        infoItemsAdapter = InfoItemsAdapter(viewModel.listLiveData,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT, onClickListener = this)
        viewModel.listLiveData.listStatusChangeNotificator.observe(this,
                ListLiveDataObserver(infoItemsAdapter))
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        recyclerView.adapter = infoItemsAdapter
    }
}