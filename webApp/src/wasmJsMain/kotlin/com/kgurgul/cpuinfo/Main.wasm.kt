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
package com.kgurgul.cpuinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.kgurgul.cpuinfo.di.initKoin
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.desktop_icon
import com.kgurgul.cpuinfo.shared.ic_android
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu
import com.kgurgul.cpuinfo.shared.ic_thrash
import kotlinx.browser.document
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadImageVector

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport(document.body!!) {
        if (preloadResources()) {
            WebApp()
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun preloadResources(): Boolean {
    val i1 = preloadImageVector(Res.drawable.desktop_icon).value != null
    val i2 = preloadImageVector(Res.drawable.ic_cpu).value != null
    val i3 = preloadImageVector(Res.drawable.ic_thrash).value != null
    val i4 = preloadImageVector(Res.drawable.ic_android).value != null
    val i5 = preloadImageVector(Res.drawable.ic_battery).value != null
    return i1 && i2 && i3 && i4 && i5
}
