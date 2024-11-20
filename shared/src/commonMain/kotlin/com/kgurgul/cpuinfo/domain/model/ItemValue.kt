package com.kgurgul.cpuinfo.domain.model

import androidx.compose.runtime.Composable
import com.kgurgul.cpuinfo.domain.model.ItemValue.NameResource
import com.kgurgul.cpuinfo.domain.model.ItemValue.NameValueResource
import com.kgurgul.cpuinfo.domain.model.ItemValue.Text
import com.kgurgul.cpuinfo.domain.model.ItemValue.ValueResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface ItemValue {

    data class Text(val name: String, val value: String) : ItemValue

    data class NameResource(val name: StringResource, val value: String) : ItemValue

    data class ValueResource(val name: String, val value: StringResource) : ItemValue

    data class NameValueResource(val name: StringResource, val value: StringResource) : ItemValue
}

fun ItemValue.getKey(): String {
    return when (this) {
        is Text -> name
        is NameResource -> name.toString()
        is ValueResource -> name
        is NameValueResource -> name.toString()
    }
}

@Composable
fun ItemValue.getName(): String {
    return when (this) {
        is Text -> name
        is NameResource -> stringResource(name)
        is ValueResource -> name
        is NameValueResource -> stringResource(name)
    }
}

@Composable
fun ItemValue.getValue(): String {
    return when (this) {
        is Text -> value
        is NameResource -> value
        is ValueResource -> stringResource(value)
        is NameValueResource -> stringResource(value)
    }
}
