package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.utils.toList
import kotlinx.browser.window
import org.koin.core.component.KoinComponent

actual class OsDataProvider actual constructor() : IOsDataProvider, KoinComponent {

    actual override suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            if (window.navigator.platform.isNotEmpty()) {
                add("Platform" to window.navigator.platform)
            }
            if (window.navigator.appCodeName.isNotEmpty()) {
                add("App code name" to window.navigator.appCodeName)
            }
            if (window.navigator.appName.isNotEmpty()) {
                add("App name" to window.navigator.appName)
            }
            if (window.navigator.appVersion.isNotEmpty()) {
                add("App version" to window.navigator.appVersion)
            }
            if (window.navigator.product.isNotEmpty()) {
                add("Product" to window.navigator.product)
            }
            if (window.navigator.productSub.isNotEmpty()) {
                add("Product sub" to window.navigator.productSub)
            }
            if (window.navigator.userAgent.isNotEmpty()) {
                add("User agent" to window.navigator.userAgent)
            }
            if (window.navigator.vendor.isNotEmpty()) {
                add("Vendor" to window.navigator.vendor)
            }
            if (window.navigator.vendorSub.isNotEmpty()) {
                add("Vendor sub" to window.navigator.vendorSub)
            }
            if (window.navigator.language.isNotEmpty()) {
                add("Language" to window.navigator.language)
            }
            val languages = window.navigator.languages.toList()
            if (languages.isNotEmpty()) {
                add("Languages" to languages.joinToString())
            }
            add("Cookies enabled" to window.navigator.cookieEnabled.toString())
        }
    }
}
