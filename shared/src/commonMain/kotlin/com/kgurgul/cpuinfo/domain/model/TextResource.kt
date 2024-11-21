package com.kgurgul.cpuinfo.domain.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface TextResource {
    data class Text(val value: String) : TextResource
    data class Resource(val value: StringResource) : TextResource
}

@Composable
fun TextResource.asString(): String = when (this) {
    is TextResource.Text -> value
    is TextResource.Resource -> stringResource(value)
}
