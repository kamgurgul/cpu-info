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
import com.kgurgul.cpuinfo.shared.os_app_code_name
import com.kgurgul.cpuinfo.shared.os_app_name
import com.kgurgul.cpuinfo.shared.os_app_version
import com.kgurgul.cpuinfo.shared.os_cookies_enabled
import com.kgurgul.cpuinfo.shared.os_history_length
import com.kgurgul.cpuinfo.shared.os_language
import com.kgurgul.cpuinfo.shared.os_languages
import com.kgurgul.cpuinfo.shared.os_pdf_viewer_enabled
import com.kgurgul.cpuinfo.shared.os_platform
import com.kgurgul.cpuinfo.shared.os_product
import com.kgurgul.cpuinfo.shared.os_product_sub
import com.kgurgul.cpuinfo.shared.os_user_agent
import com.kgurgul.cpuinfo.shared.os_vendor_sub
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.utils.ResourceUtils
import com.kgurgul.cpuinfo.utils.isPdfViewerEnabled
import com.kgurgul.cpuinfo.utils.toList
import kotlinx.browser.window

actual class OsDataProvider actual constructor() : IOsDataProvider {

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            if (window.navigator.platform.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_platform, window.navigator.platform))
            }
            if (window.navigator.appCodeName.isNotEmpty()) {
                add(
                    ItemValue.NameResource(
                        Res.string.os_app_code_name,
                        window.navigator.appCodeName,
                    )
                )
            }
            if (window.navigator.appName.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_app_name, window.navigator.appName))
            }
            if (window.navigator.appVersion.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_app_version, window.navigator.appVersion))
            }
            if (window.navigator.product.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_product, window.navigator.product))
            }
            if (window.navigator.productSub.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_product_sub, window.navigator.productSub))
            }
            if (window.navigator.userAgent.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_user_agent, window.navigator.userAgent))
            }
            if (window.navigator.vendor.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.vendor, window.navigator.vendor))
            }
            if (window.navigator.vendorSub.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_vendor_sub, window.navigator.vendorSub))
            }
            if (window.navigator.language.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_language, window.navigator.language))
            }
            val languages = window.navigator.languages.toList()
            if (languages.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.os_languages, languages.joinToString()))
            }
            add(
                ItemValue.NameValueResource(
                    Res.string.os_cookies_enabled,
                    ResourceUtils.getYesNoStringResource(window.navigator.cookieEnabled),
                )
            )
            add(
                ItemValue.NameValueResource(
                    Res.string.os_pdf_viewer_enabled,
                    ResourceUtils.getYesNoStringResource(isPdfViewerEnabled()),
                )
            )
            add(
                ItemValue.NameResource(
                    Res.string.os_history_length,
                    window.history.length.toString(),
                )
            )
        }
    }
}
