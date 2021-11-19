package com.kgurgul.cpuinfo.features.applications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.theme.CpuInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewApplicationsFragment : Fragment() {

    private val viewModel: NewApplicationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            id = R.id.applications_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                CpuInfoTheme {
                    ApplicationsScreen(
                        viewModel = viewModel,
                        onAppClicked = viewModel::onApplicationClicked
                    )
                }
            }
        }
    }
}