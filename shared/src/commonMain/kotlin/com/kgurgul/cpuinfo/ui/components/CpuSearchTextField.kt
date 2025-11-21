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
package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kgurgul.cpuinfo.features.applications.ApplicationsScreenTestData
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.search
import com.kgurgul.cpuinfo.shared.search_clear
import com.kgurgul.cpuinfo.shared.search_close
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.stringResource

@Composable
fun CpuSearchTextField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClosed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = { keyboardController?.hide() }

    CpuTextField(
        colors =
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface,
            ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(Res.string.search),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (searchQuery.isNotEmpty()) {
                        onSearchQueryChanged("")
                    } else {
                        onSearchClosed()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription =
                        if (searchQuery.isNotEmpty()) {
                            stringResource(Res.string.search_clear)
                        } else {
                            stringResource(Res.string.search_close)
                        },
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        onValueChange = { if ("\n" !in it) onSearchQueryChanged(it) },
        modifier =
            modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .padding(spacingSmall)
                .focusRequester(focusRequester)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        if (searchQuery.isBlank()) return@onKeyEvent false
                        onSearchExplicitlyTriggered()
                        true
                    } else {
                        false
                    }
                }
                .testTag(ApplicationsScreenTestData.SEARCH_TEST_TAG),
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                    if (searchQuery.isBlank()) return@KeyboardActions
                    onSearchExplicitlyTriggered()
                }
            ),
        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}
