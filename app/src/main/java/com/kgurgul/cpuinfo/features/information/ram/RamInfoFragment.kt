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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import com.kgurgul.cpuinfo.utils.*
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment which contains RAM info. For older android there is also available cleaning option.
 *
 * @author kgurgul
 */
@AndroidEntryPoint
class RamInfoFragment : BaseRvFragment() {

    private val viewModel: RamInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startProvidingData()
    }

    override fun onStop() {
        viewModel.stopProvidingData()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Cleaning options works only for old Android
        runOnApiBelow(24) {
            inflater.inflate(R.menu.ram_menu, menu)
        }
        runOnApiAbove(18) {
            inflater.inflate(R.menu.info_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_gc -> {
                    viewModel.clearRam()
                    Snackbar.make(mainContainer, getString(R.string.running_gc),
                            Snackbar.LENGTH_SHORT).show()
                    true
                }
                R.id.action_export_to_csv -> {
                    createSafFile(MIME_TEXT_PLAIN, DUMP_FILENAME, RC_CREATE_FILE)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun setupRecyclerViewAdapter() {
        val infoItemsAdapter = InfoItemsAdapter(viewModel.listLiveData,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT, onClickListener = this)
        viewModel.listLiveData.listStatusChangeNotificator.observe(viewLifecycleOwner,
                ListLiveDataObserver(infoItemsAdapter))
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        recyclerView.adapter = infoItemsAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_CREATE_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.also { uri ->
                        viewModel.saveListToFile(uri)
                    }
                }
            }
        }
    }

    companion object {
        private const val RC_CREATE_FILE = 100
        private const val DUMP_FILENAME = "ram_info.txt"
    }
}