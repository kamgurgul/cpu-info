package com.kgurgul.cpuinfo.features.applications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.uninstallApp
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewApplicationsFragment : Fragment() {

    private val viewModel: NewApplicationsViewModel by viewModels()

    private val uninstallReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.onRefreshApplications()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerUninstallBroadcast()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CpuInfoTheme {
                    ApplicationsScreen(
                        viewModel = viewModel,
                        onAppClicked = viewModel::onApplicationClicked,
                        onRefreshApplications = viewModel::onRefreshApplications,
                        onSnackbarDismissed = viewModel::onSnackbarDismissed,
                        onAppUninstallClicked = viewModel::onAppUninstallClicked,
                        onAppSettingsClicked = viewModel::onAppSettingsClicked,
                        onNativeLibsClicked = viewModel::onNativeLibsClicked,
                        onSystemAppsSwitched = viewModel::onSystemAppsSwitched,
                        onSortOrderChange = viewModel::onSortOrderChange,
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObservers()
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(uninstallReceiver)
        super.onDestroy()
    }

    private fun registerObservers() {
        viewModel.events.observe(viewLifecycleOwner, ::handleEvent)
    }

    @SuppressLint("InflateParams")
    private fun handleEvent(event: NewApplicationsViewModel.Event) {
        when (event) {
            is NewApplicationsViewModel.Event.OpenApp -> {
                val intent = requireContext().packageManager.getLaunchIntentForPackage(
                    event.packageName
                )
                if (intent != null) {
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        viewModel.onCannotOpenApp()
                    }
                } else {
                    viewModel.onCannotOpenApp()
                }
            }

            is NewApplicationsViewModel.Event.OpenAppSettings -> {
                val uri = Uri.fromParts("package", event.packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Timber.e("Can't open app settings")
                }
            }

            is NewApplicationsViewModel.Event.UninstallApp -> {
                requireActivity().uninstallApp(event.packageName)
            }

            is NewApplicationsViewModel.Event.ShowNativeLibraries -> {
                val dialogLayout = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_native_libs, null)
                val arrayAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.item_native_libs,
                    R.id.native_name_tv,
                    event.nativeLibs,
                )
                dialogLayout.findViewById<ListView>(R.id.dialog_lv).apply {
                    adapter = arrayAdapter
                    onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                        Utils.searchInGoogle(requireContext(), event.nativeLibs[position])
                    }
                }
                MaterialAlertDialogBuilder(requireContext())
                    .setPositiveButton(R.string.ok) { dialog, _ -> dialog.cancel() }
                    .setView(dialogLayout)
                    .create()
                    .show()
            }
        }
    }

    private fun registerUninstallBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        requireActivity().registerReceiver(uninstallReceiver, intentFilter)
    }
}