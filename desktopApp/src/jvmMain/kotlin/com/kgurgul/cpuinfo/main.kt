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

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kgurgul.cpuinfo.di.initKoin
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_name
import com.kgurgul.cpuinfo.shared.desktop_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skia.DirectContext

fun main() {
    initKoin()
    if (System.getProperty("os.name").equals("Linux", ignoreCase = true)) {
        try {
            DirectContext.makeGL().flush().close()
        } catch (_: Throwable) {
            System.setProperty("skiko.renderApi", "SOFTWARE")
        }
    }
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.desktop_icon),
        ) {
            DesktopApp()
        }
    }
}
