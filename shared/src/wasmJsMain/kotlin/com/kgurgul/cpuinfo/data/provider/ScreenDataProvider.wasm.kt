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
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.screen_available_height
import com.kgurgul.cpuinfo.shared.screen_available_width
import com.kgurgul.cpuinfo.shared.screen_color_depth
import com.kgurgul.cpuinfo.shared.screen_inner_height
import com.kgurgul.cpuinfo.shared.screen_inner_width
import com.kgurgul.cpuinfo.shared.screen_pixel_depth
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class ScreenDataProvider actual constructor() : IScreenDataProvider {

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.screen_inner_width, "${window.innerWidth}px"))
            add(ItemValue.NameResource(Res.string.screen_inner_height, "${window.innerHeight}px"))
            add(
                ItemValue.NameResource(
                    Res.string.screen_available_width,
                    "${window.screen.availWidth}px",
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.screen_available_height,
                    "${window.screen.availHeight}px",
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.screen_color_depth,
                    window.screen.colorDepth.toString(),
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.screen_pixel_depth,
                    window.screen.pixelDepth.toString(),
                )
            )
        }
    }

    actual override fun getOrientationFlow(): Flow<String> = emptyFlow()
}
