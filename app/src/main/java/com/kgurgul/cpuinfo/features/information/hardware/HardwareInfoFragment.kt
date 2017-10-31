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

package com.kgurgul.cpuinfo.features.information.hardware

import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import javax.inject.Inject

/**
 * Fragment responsible for hardware info. It also contains [BroadcastReceiver] for AC connection.
 *
 * @author kgurgul
 */
class HardwareInfoFragment : BaseRvFragment() {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<HardwareInfoViewModel>

    private val viewModel: HardwareInfoViewModel by lazy {
        ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(HardwareInfoViewModel::class.java)
    }

    private val infoItemsAdapter: InfoItemsAdapter by lazy {
        InfoItemsAdapter(context, viewModel.dataObservableList,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT)
    }

    private val powerReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            viewModel.refreshHardwareInfo()
        }
    }

    override fun onStart() {
        super.onStart()
        infoItemsAdapter.registerListChangeNotifier()
    }

    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED")
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED")

        activity.registerReceiver(powerReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        activity.unregisterReceiver(powerReceiver)
    }

    override fun onStop() {
        infoItemsAdapter.unregisterListChangeNotifier()
        super.onStop()
    }

    override fun setupRecyclerViewAdapter() {
        recyclerView.addItemDecoration(DividerItemDecoration(context))
        recyclerView.adapter = infoItemsAdapter
    }
}