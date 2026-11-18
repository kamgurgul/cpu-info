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
package com.kgurgul.cpuinfo.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator

/**
 * Content key of the entry on top of the back stack. Provide it around the `NavDisplay` showing the
 * entries.
 */
val LocalTopEntryKey = compositionLocalOf<Any?> { null }

/** Content key of the current entry, see [navEntryKeyNavEntryDecorator]. */
val LocalNavEntryKey = compositionLocalOf<Any?> { null }

/**
 * Whether the current entry is the one on top of the back stack. It follows the back stack, so the
 * entry stays the top one during a back gesture until the gesture completes. Always `true` outside
 * of a `NavDisplay`.
 */
@Composable fun isTopEntry(): Boolean = LocalNavEntryKey.current == LocalTopEntryKey.current

/** Provides [LocalNavEntryKey] to the entries. */
fun <T : Any> navEntryKeyNavEntryDecorator(): NavEntryDecorator<T> =
    NavEntryDecorator(
        decorate = { entry: NavEntry<T> ->
            CompositionLocalProvider(LocalNavEntryKey provides entry.contentKey) {
                entry.Content()
            }
        }
    )
