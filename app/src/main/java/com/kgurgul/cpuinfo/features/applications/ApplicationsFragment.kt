package com.kgurgul.cpuinfo.features.applications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.uninstallApp
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ApplicationsFragment : Fragment() {

    private val viewModel: ApplicationsViewModel by viewModels()

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

    private fun handleEvent(event: ApplicationsViewModel.Event) {
        when (event) {
            is ApplicationsViewModel.Event.OpenApp -> {
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

            is ApplicationsViewModel.Event.OpenAppSettings -> {
                val uri = Uri.fromParts("package", event.packageName, null)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Timber.e("Can't open app settings")
                }
            }

            is ApplicationsViewModel.Event.UninstallApp -> {
                requireActivity().uninstallApp(event.packageName)
            }

            is ApplicationsViewModel.Event.SearchNativeLib -> {
                Utils.searchInGoogle(requireContext(), event.name)
            }
        }
    }
}