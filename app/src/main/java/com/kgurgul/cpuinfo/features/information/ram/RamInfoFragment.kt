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

import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import com.kgurgul.cpuinfo.utils.nonNullContext
import javax.inject.Inject

/**
 * Fragment which contains RAM info. For older android there is also available cleaning option.
 *
 * @author kgurgul
 */
class RamInfoFragment : BaseRvFragment() {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<RamInfoViewModel>

    private val viewModel: RamInfoViewModel by lazy {
        ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(RamInfoViewModel::class.java)
    }

    private val infoItemsAdapter: InfoItemsAdapter by lazy {
        InfoItemsAdapter(nonNullContext(), viewModel.dataObservableList,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT)
    }

    private lateinit var mainContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        mainContainer = view.findViewById(R.id.main_container)
        return view
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Cleaning options works only for old Android
        if (Build.VERSION.SDK_INT < 24) {
            inflater.inflate(R.menu.ram_menu, menu)
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
                else -> super.onOptionsItemSelected(item)
            }

    override fun setupRecyclerViewAdapter() {
        recyclerView.addItemDecoration(DividerItemDecoration(nonNullContext()))
        recyclerView.adapter = infoItemsAdapter
    }
}