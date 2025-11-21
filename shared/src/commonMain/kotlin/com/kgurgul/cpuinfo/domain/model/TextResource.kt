/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.domain.model

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface TextResource {
    data class Text(val value: String) : TextResource

    data class Resource(val value: StringResource) : TextResource

    data class Formatted(val format: StringResource, val args: List<Any>) : TextResource
}

@Composable
fun TextResource.asString(): String =
    when (this) {
        is TextResource.Text -> value
        is TextResource.Resource -> stringResource(value)
        is TextResource.Formatted -> stringResource(format, *resolveArgs(args))
    }
