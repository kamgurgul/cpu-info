package com.kgurgul.cpuinfo.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.waitForAsyncContent() {
    device.wait(Until.hasObject(By.res("cpu_info_lazy_column")), 5_000)
    val element = UiScrollable(UiSelector().scrollable(true))
    element.scrollToEnd(3)
}