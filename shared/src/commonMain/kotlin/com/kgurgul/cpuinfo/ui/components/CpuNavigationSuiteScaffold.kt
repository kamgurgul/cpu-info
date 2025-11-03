package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CpuNavigationSuiteScaffold(
    navigationItems: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val navigationSuitColors = NavigationSuiteDefaults.colors(
        shortNavigationBarContainerColor = MaterialTheme.colorScheme.primary,
        shortNavigationBarContentColor = MaterialTheme.colorScheme.onPrimary,
        wideNavigationRailColors = WideNavigationRailDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationBarContainerColor = MaterialTheme.colorScheme.primary,
        navigationBarContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationRailContainerColor = MaterialTheme.colorScheme.primary,
        navigationRailContentColor = MaterialTheme.colorScheme.onPrimary,
    )
    NavigationSuiteScaffold(
        navigationItems = navigationItems,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        navigationSuiteColors = navigationSuitColors,
        navigationItemVerticalArrangement = Arrangement.Center,
        modifier = modifier,
        content = content,
    )
}

object CpuNavigationSuiteScaffoldDefault {

    @Composable
    fun itemColors() = NavigationItemColors(
        selectedIconColor = MaterialTheme.colorScheme.onSecondary,
        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
        selectedIndicatorColor = MaterialTheme.colorScheme.secondary,
        unselectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledIconColor = MaterialTheme.colorScheme.onPrimary,
        disabledTextColor = MaterialTheme.colorScheme.onPrimary,
    )
}
