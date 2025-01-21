package com.kgurgul.cpuinfo.features.settings.licenses

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.licenses
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LicensesScreen(viewModel: LicensesViewModel = koinViewModel()) {

}

@Composable
fun LicensesScreen(uiState: LicensesViewModel.UiState) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.licenses),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->

    }
}
