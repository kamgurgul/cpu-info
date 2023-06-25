package com.kgurgul.cpuinfo.features.applications

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
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
                    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
                    ApplicationsScreen(
                        uiState = uiState,
                        onAppClicked = viewModel::onApplicationClicked,
                        onRefreshApplications = viewModel::onRefreshApplications,
                        onSnackbarDismissed = viewModel::onSnackbarDismissed,
                        onCardExpanded = viewModel::onCardExpanded,
                        onCardCollapsed = viewModel::onCardCollapsed,
                        onAppUninstallClicked = viewModel::onAppUninstallClicked,
                        onAppSettingsClicked = viewModel::onAppSettingsClicked,
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObservers()
    }

    private fun registerObservers() {
        viewModel.events.observe(viewLifecycleOwner, ::handleEvent)
    }

    private fun handleEvent(event: NewApplicationsViewModel.Event) {
        when (event) {
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
        }
    }

    private fun registerUninstallBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        requireActivity().registerReceiver(uninstallReceiver, intentFilter)
    }
}