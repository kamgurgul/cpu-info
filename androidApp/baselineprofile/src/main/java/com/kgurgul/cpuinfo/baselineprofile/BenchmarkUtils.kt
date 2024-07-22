package com.kgurgul.cpuinfo.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.waitForAsyncContent() {
    device.wait(Until.hasObject(By.res("cpu_info_lazy_column")), 5_000)
    val contentList = device.findObject(By.res("cpu_info_lazy_column"))
    contentList.wait(Until.hasObject(By.res("cpu_info_socket_name")), 5_000)
}