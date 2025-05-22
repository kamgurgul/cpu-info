package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CpuPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state,
        )
    },
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier.pullToRefresh(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            enabled = enabled,
        ),
        contentAlignment = contentAlignment,
    ) {
        content()
        indicator()
    }
}

@Preview
@Composable
fun CpuPullToRefreshBoxPreview() {
    Column {
        CpuInfoTheme {
            CpuPullToRefreshBox(
                isRefreshing = true,
                onRefresh = {},
            ) {}
        }
        CpuInfoTheme(
            useDarkTheme = true
        ) {
            CpuPullToRefreshBox(
                isRefreshing = true,
                onRefresh = {},
            ) {}
        }
    }
}
