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
