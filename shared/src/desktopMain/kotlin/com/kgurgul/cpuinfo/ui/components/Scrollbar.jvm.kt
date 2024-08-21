package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: ScrollState
) = androidx.compose.foundation.VerticalScrollbar(
    adapter = rememberScrollbarAdapter(scrollState),
    style = defaultScrollbarStyle().copy(
        unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.30f),
        hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f),
    ),
    modifier = modifier,
)

@Composable
actual fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: LazyListState
) = androidx.compose.foundation.VerticalScrollbar(
    adapter = rememberScrollbarAdapter(scrollState),
    style = defaultScrollbarStyle().copy(
        unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.30f),
        hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f),
    ),
    modifier = modifier,
)