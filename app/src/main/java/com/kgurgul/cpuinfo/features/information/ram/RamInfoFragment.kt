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

package com.kgurgul.cpuinfo.features.information.ram

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentRecyclerViewBinding
import com.kgurgul.cpuinfo.features.information.base.BaseFragment
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RamInfoFragment : BaseFragment<FragmentRecyclerViewBinding>(R.layout.fragment_recycler_view) {

    private val viewModel: RamInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = RamInfoEpoxyController(requireContext())
        binding.recyclerView.adapter = controller.adapter

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                runOnApiBelow(24) {
                    menuInflater.inflate(R.menu.ram_menu, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_gc -> {
                        viewModel.onClearRamClicked()
                        Snackbar.make(
                            binding.mainContainer, getString(R.string.running_gc),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.viewState.observe(viewLifecycleOwner) { controller.setData(it) }
    }
}