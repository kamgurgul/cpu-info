package com.kgurgul.cpuinfo.features.processes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProcessesFragment : Fragment() {

    private val viewModel: ProcessesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CpuInfoTheme {
                    ProcessesScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}