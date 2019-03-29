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

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentProcessesBinding
import com.kgurgul.cpuinfo.di.Injectable
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.utils.DividerItemDecoration
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import com.kgurgul.cpuinfo.utils.viewModelProvider
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
    private lateinit var binding: FragmentProcessesBinding
    private lateinit var processesAdapter: ProcessesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelProvider(viewModelInjectionFactory)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_processes, container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecyclerView()
        return binding.root
    }

    /**
     * Setup for [RecyclerView]
     */
    private fun setupRecyclerView() {
        processesAdapter = ProcessesAdapter(viewModel.processList)
        viewModel.processList.listStatusChangeNotificator.observe(viewLifecycleOwner,
                ListLiveDataObserver(processesAdapter))

        val rvLayoutManager = LinearLayoutManager(context)
        binding.apply {
            recyclerView.layoutManager = rvLayoutManager
            recyclerView.adapter = processesAdapter
            recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
            (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startProcessRefreshing()
    }

    override fun onStop() {
        viewModel.stopProcessRefreshing()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.process_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_sorting -> {
                    viewModel.changeProcessSorting()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}