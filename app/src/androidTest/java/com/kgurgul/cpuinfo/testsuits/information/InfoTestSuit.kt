/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kgurgul.cpuinfo.testsuits.information

import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.BaseTestSuit
import com.kgurgul.cpuinfo.core.getString
import com.kgurgul.cpuinfo.screens.HardwareScreen
import com.kgurgul.cpuinfo.uitestutils.conditionwatcher.waitForView
import org.junit.Test

class InfoTestSuit : BaseTestSuit() {

    private val hardwareScreen = HardwareScreen()

    @Test
    fun checkToolbarTitle() {
        hardwareScreen.isToolbarTitleValid(getString(R.string.hardware))
    }

    @Test
    fun checkCpuTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.cpu))
        waitForView(hardwareScreen.recyclerView).toMatch(hasMinimumChildCount(3))
    }

    @Test
    fun checkGpuTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.gpu))
        hardwareScreen.hasTextOnPosition(getString(R.string.gles_version), 0)
    }

    @Test
    fun checkRamTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.ram))
        hardwareScreen.hasTextOnPosition(getString(R.string.total_memory), 0)
        hardwareScreen.hasTextOnPosition(getString(R.string.available_memory), 1)
        hardwareScreen.hasTextOnPosition(getString(R.string.threshold), 2)
    }

    @Test
    fun checkStorageTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.storage))
        hardwareScreen.hasAtLeastRvElements(1)
    }

    @Test
    fun checkScreenTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.screen))
        hardwareScreen.hasTextOnPosition(getString(R.string.screen_class), 0)
        hardwareScreen.hasTextOnPosition(getString(R.string.density_class), 1)
    }

    @Test
    fun checkAndroidTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.android))
        hardwareScreen.hasTextOnPosition(getString(R.string.version), 0)
        hardwareScreen.hasTextOnPosition("SDK", 1)
    }

    @Test
    fun checkHardwareTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.hardware))
        hardwareScreen.hasTextOnPosition(getString(R.string.battery), 0)
        hardwareScreen.hasTextOnPosition(getString(R.string.level), 1)
    }

    @Test
    fun checkSensorsTab() {
        hardwareScreen.tapTabWithTitle(getString(R.string.sensors))
        hardwareScreen.hasAtLeastRvElements(1)
    }
}