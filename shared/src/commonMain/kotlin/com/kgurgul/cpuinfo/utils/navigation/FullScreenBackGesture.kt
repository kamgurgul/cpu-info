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
import androidx.compose.ui.Modifier

/**
 * Lets the user swipe back from anywhere in the content, not only from the edge of the screen, like
 * in native iOS 26 apps. The other platforms skip it, there it returns the modifier unchanged.
 */
@Composable expect fun Modifier.fullScreenBackGesture(): Modifier
