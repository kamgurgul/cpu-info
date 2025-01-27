package com.kgurgul.cpuinfo.domain.model

import androidx.compose.runtime.Composable
import com.kgurgul.cpuinfo.domain.model.ItemValue.FormattedNameResource
import com.kgurgul.cpuinfo.domain.model.ItemValue.FormattedNameValueResource
import com.kgurgul.cpuinfo.domain.model.ItemValue.FormattedValueResource
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

    data class FormattedNameResource(
        val nameFormat: StringResource,
        val args: List<Any>,
        val value: String,
    ) : ItemValue

    data class FormattedValueResource(
        val name: String,
        val valueFormat: StringResource,
        val args: List<Any>,
    ) : ItemValue

    data class FormattedNameValueResource(
        val nameFormat: StringResource,
        val args: List<Any>,
        val value: StringResource,
    ) : ItemValue
}

fun ItemValue.getKey(): String {
    return when (this) {
        is Text -> name
        is NameResource -> name.toString()
        is ValueResource -> name
        is NameValueResource -> name.toString()
        is FormattedNameResource -> nameFormat.toString()
        is FormattedValueResource -> name
        is FormattedNameValueResource -> nameFormat.toString()
    }
}

@Composable
fun ItemValue.getName(): String {
    return when (this) {
        is Text -> name
        is NameResource -> stringResource(name)
        is ValueResource -> name
        is NameValueResource -> stringResource(name)
        is FormattedNameResource -> stringResource(nameFormat, *resolveArgs(args))
        is FormattedValueResource -> name
        is FormattedNameValueResource -> stringResource(nameFormat, *resolveArgs(args))
    }
}

@Composable
fun ItemValue.getValue(): String {
    return when (this) {
        is Text -> value
        is NameResource -> value
        is ValueResource -> stringResource(value)
        is NameValueResource -> stringResource(value)
        is FormattedNameResource -> value
        is FormattedValueResource -> stringResource(valueFormat, *resolveArgs(args))
        is FormattedNameValueResource -> stringResource(value)
    }
}

@Composable
fun resolveArgs(args: List<Any>): Array<String> {
    return args.map {
        when (it) {
            is StringResource -> stringResource(it)
            is String -> it
            else -> it.toString()
        }
    }.toTypedArray()
}
