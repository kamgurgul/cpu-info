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

package com.kgurgul.cpuinfo.features.applications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentApplicationsBinding
import com.kgurgul.cpuinfo.di.Injectable
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.utils.DividerItemDecoration
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import com.kgurgul.cpuinfo.utils.setDelayedRefreshingState
import com.kgurgul.cpuinfo.utils.viewModelProvider
import com.kgurgul.cpuinfo.utils.wrappers.EventObserver
import com.kgurgul.cpuinfo.widgets.swiperv.SwipeMenuRecyclerView
import java.io.File
import javax.inject.Inject

/**
 * Activity for apps list.
 *
 * @author kgurgul
 */
class ApplicationsFragment : Fragment(), Injectable, ApplicationsAdapter.ItemClickListener {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<ApplicationsViewModel>

    private lateinit var viewModel: ApplicationsViewModel
    private lateinit var binding: FragmentApplicationsBinding
    private lateinit var applicationsAdapter: ApplicationsAdapter

    private val uninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.refreshApplicationsList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = viewModelProvider(viewModelInjectionFactory)
        viewModel.refreshApplicationsList()
        registerUninstallBroadcast()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_applications, container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimaryDark)
        initObservables()
        setupRecyclerView()
        return binding.root
    }

    /**
     * Setup for [SwipeMenuRecyclerView]
     */
    private fun setupRecyclerView() {
        applicationsAdapter = ApplicationsAdapter(viewModel.applicationList, this)
        viewModel.applicationList.listStatusChangeNotificator.observe(viewLifecycleOwner,
                ListLiveDataObserver(applicationsAdapter))

        binding.recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = applicationsAdapter
            addItemDecoration(DividerItemDecoration(requireContext()))
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    /**
     * Register all fields from [ApplicationsViewModel] which should be observed
     */
    private fun initObservables() {
        viewModel.shouldStartStorageServiceEvent.observe(viewLifecycleOwner, EventObserver {
            StorageUsageService.startService(requireContext(), viewModel.applicationList)
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.setDelayedRefreshingState(it)
        })
    }

    /**
     * Register broadcast receiver for uninstalling apps
     */
    private fun registerUninstallBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        requireActivity().registerReceiver(uninstallReceiver, intentFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.apps_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_sorting -> {
                    viewModel.changeAppsSorting()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    /**
     * Try to open clicked app. In case of error show [Snackbar].
     */
    override fun appOpenClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        // Block self opening
        if (appInfo.packageName == requireContext().packageName) {
            Snackbar.make(binding.mainContainer, getString(R.string.cpu_open),
                    Snackbar.LENGTH_SHORT).show()
            return
        }

        val intent = requireContext().packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (intent != null) {
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(binding.mainContainer, getString(R.string.app_open),
                        Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(binding.mainContainer, getString(R.string.app_open),
                    Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Open settings activity for selected app
     */
    override fun appSettingsClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        val uri = Uri.fromParts("package", appInfo.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        startActivity(intent)
    }

    /**
     * Try to uninstall selected app
     */
    override fun appUninstallClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        if (appInfo.packageName == requireContext().packageName) {
            Snackbar.make(binding.mainContainer, getString(R.string.cpu_uninstall),
                    Snackbar.LENGTH_SHORT).show()
            return
        }

        val uri = Uri.fromParts("package", appInfo.packageName, null)
        val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri)
        startActivity(uninstallIntent)
    }

    /**
     * Open dialog with native lib list and open google if user taps on it
     */
    override fun appNativeLibsClicked(nativeDir: String) {
        showNativeListDialog(nativeDir)
    }

    /**
     * Create dialog with native libraries list
     */
    @SuppressLint("InflateParams")
    private fun showNativeListDialog(nativeLibsDir: String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialog_native_libs, null)
        val nativeDirFile = File(nativeLibsDir)
        val libs = nativeDirFile.listFiles().map { it.name }

        val listView: ListView = dialogLayout.findViewById(R.id.dialog_lv)
        val arrayAdapter = ArrayAdapter<String>(requireContext(), R.layout.item_native_libs,
                R.id.native_name_tv, libs)
        listView.adapter = arrayAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Utils.searchInGoogle(requireContext(), libs[position])
        }
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(dialogLayout)
        val alert = builder.create()
        alert.show()
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(uninstallReceiver)
        super.onDestroy()
    }
}