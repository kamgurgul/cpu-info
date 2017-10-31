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

package com.kgurgul.cpuinfo.features.processes

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.*
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.databinding.FragmentProcessesBinding
import com.kgurgul.cpuinfo.di.Injectable
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.utils.AutoClearedValue
import javax.inject.Inject

/**
 * Displays list of running processes (works only to the API 24)
 *
 * @author kgurgul
 */
class ProcessesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<ProcessesViewModel>

    private lateinit var viewModel: ProcessesViewModel
    private lateinit var binding: AutoClearedValue<FragmentProcessesBinding>
    private lateinit var processesAdapter: AutoClearedValue<ProcessesAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(ProcessesViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = AutoClearedValue(this,
                DataBindingUtil.inflate(inflater, R.layout.fragment_processes, container, false))
        binding.get().viewModel = viewModel
        setupRecyclerView()
        return binding.get().root
    }

    /**
     * Setup for [RecyclerView]
     */
    private fun setupRecyclerView() {
        processesAdapter = AutoClearedValue(this, ProcessesAdapter(viewModel.processList))

        val rvLayoutManager = LinearLayoutManager(context)
        binding.get().recyclerView.layoutManager = rvLayoutManager

        binding.get().recyclerView.adapter = processesAdapter.get()
        binding.get().recyclerView.addItemDecoration(DividerItemDecoration(context))
        (binding.get().recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun onStart() {
        super.onStart()
        processesAdapter.get().registerListChangeNotifier()
        viewModel.startProcessRefreshing()
    }

    override fun onStop() {
        viewModel.stopProcessRefreshing()
        processesAdapter.get().unregisterListChangeNotifier()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.process_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sorting -> {
                viewModel.changeProcessSorting()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}