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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable actual fun VerticalScrollbar(modifier: Modifier, scrollState: ScrollState) = Unit

@Composable actual fun VerticalScrollbar(modifier: Modifier, scrollState: LazyListState) = Unit

@Composable actual fun HorizontalScrollbar(modifier: Modifier, scrollState: ScrollState) = Unit

@Composable actual fun HorizontalScrollbar(modifier: Modifier, scrollState: LazyListState) = Unit
