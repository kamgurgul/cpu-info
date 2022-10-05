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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentProcessesBinding
import com.kgurgul.cpuinfo.features.information.base.BaseFragment
import com.kgurgul.cpuinfo.utils.DividerItemDecoration
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProcessesFragment : BaseFragment<FragmentProcessesBinding>(R.layout.fragment_processes) {

    private val viewModel: ProcessesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecyclerView()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.process_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_sorting -> {
                        viewModel.changeProcessSorting()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    /**
     * Setup for [RecyclerView]
     */
    private fun setupRecyclerView() {
        val processesAdapter = ProcessesAdapter(viewModel.processList)
        viewModel.processList.listStatusChangeNotificator.observe(
            viewLifecycleOwner,
            ListLiveDataObserver(processesAdapter)
        )

        val rvLayoutManager = LinearLayoutManager(requireContext())
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
}