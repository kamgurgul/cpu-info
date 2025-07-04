package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SurfaceTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CpuTopAppBar(
        title = if (title != null) {
            {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else null,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Composable
private fun defaultTopAppColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    scrolledContainerColor = MaterialTheme.colorScheme.primary,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
)

@Composable
fun PrimaryTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CpuTopAppBar(
        title = if (title != null) {
            {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else null,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = defaultTopAppColors(),
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Composable
fun PrimaryTopAppBar(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CpuTopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = defaultTopAppColors(),
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Composable
private fun CpuTopAppBar(
    colors: TopAppBarColors,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { title?.invoke() },
        navigationIcon = { navigationIcon?.invoke() },
        actions = actions,
        colors = colors,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Preview
@Composable
fun TopAppBarPreview() {
    CpuInfoTheme {
        Column {
            SurfaceTopAppBar(
                title = "Title",
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                },
            )
            PrimaryTopAppBar(
                title = "Title",
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    }
}
