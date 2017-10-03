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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.databinding.FragmentApplicationsBinding
import com.kgurgul.cpuinfo.di.Injectable
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.utils.AutoClearedValue
import com.kgurgul.cpuinfo.utils.Utils
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
    private lateinit var binding: AutoClearedValue<FragmentApplicationsBinding>
    private lateinit var applicationsAdapter: AutoClearedValue<ApplicationsAdapter>

    private val uninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.refreshApplicationsList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(ApplicationsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = AutoClearedValue(this,
                DataBindingUtil.inflate(inflater, R.layout.fragment_applications, container, false))
        binding.get().viewModel = viewModel
        binding.get().swipeRefreshLayout.setOnRefreshListener { viewModel.refreshApplicationsList() }
        binding.get().swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimaryDark)
        initObservables()
        setupRecyclerView()
        return binding.get().root
    }

    override fun onStart() {
        super.onStart()
        registerUninstallBroadcast()
    }

    override fun onStop() {
        activity.unregisterReceiver(uninstallReceiver)
        super.onStop()
    }

    override fun onDestroyView() {
        // Fix for buggy SwipeMenuRecyclerView which can leak. More investigation needed.
        binding.get().recyclerView.adapter = null
        super.onDestroyView()
    }

    /**
     * Setup for [SwipeMenuRecyclerView]
     */
    private fun setupRecyclerView() {
        applicationsAdapter = AutoClearedValue(this,
                ApplicationsAdapter(context, viewModel.applicationList, this))

        val rvLayoutManager = LinearLayoutManager(context)
        binding.get().recyclerView.layoutManager = rvLayoutManager

        binding.get().recyclerView.adapter = applicationsAdapter.get()
        binding.get().recyclerView.addItemDecoration(DividerItemDecoration(context))
        (binding.get().recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        lifecycle.addObserver(applicationsAdapter.get())
    }

    /**
     * Register all fields from [ApplicationsViewModel] which should be observed
     */
    private fun initObservables() {
        viewModel.shouldStartStorageService.observe(this, Observer {
            StorageUsageService.startService(context, viewModel.applicationList)
        })
    }

    /**
     * Register broadcast receiver for uninstalling apps
     */
    private fun registerUninstallBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        activity.registerReceiver(uninstallReceiver, intentFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.apps_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sorting -> {
                viewModel.changeAppsSorting()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Try to open clicked app. In case of error show [Snackbar].
     */
    override fun appOpenClicked(position: Int) {
        val appInfo = viewModel.applicationList[position]
        // Block self opening
        if (appInfo.packageName == context.packageName) {
            Snackbar.make(binding.get().mainContainer, getString(R.string.cpu_open),
                    Snackbar.LENGTH_SHORT).show()
            return
        }

        val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (intent != null) {
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(binding.get().mainContainer, getString(R.string.app_open),
                        Snackbar.LENGTH_SHORT).show()
            }
        } else {
            Snackbar.make(binding.get().mainContainer, getString(R.string.app_open),
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
        if (appInfo.packageName == context.packageName) {
            Snackbar.make(binding.get().mainContainer, getString(R.string.cpu_uninstall),
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
    override fun appNativeLibsClicked(nativeFile: File) {
        showNativeListDialog(nativeFile)
    }

    /**
     * Create dialog with native libraries list
     */
    private fun showNativeListDialog(nativeLibsDir: File) {
        val builder = AlertDialog.Builder(context, R.style.CustomAppCompatAlertDialogStyle)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialog_native_libs, null)
        val libs = nativeLibsDir.listFiles().map { it.name }

        val listView: ListView = dialogLayout.findViewById(R.id.dialog_lv)
        val arrayAdapter = ArrayAdapter<String>(context, R.layout.item_native_libs,
                R.id.native_name_tv, libs)
        listView.adapter = arrayAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Utils.searchInGoogle(context, libs[position])
        }
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        builder.setView(dialogLayout)
        val alert = builder.create()
        alert.show()
    }
}