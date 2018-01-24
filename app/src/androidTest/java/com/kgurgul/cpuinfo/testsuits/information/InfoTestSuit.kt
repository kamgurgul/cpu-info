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

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.getString
import com.kgurgul.cpuinfo.features.HostActivity
import com.kgurgul.cpuinfo.screens.HardwareScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Test suit which covers whole hardware section
 *
 * @author kgurgul
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class InfoTestSuit {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(HostActivity::class.java)

    private val hardwareScreen = HardwareScreen()

    @Test
    fun checkToolbarTitle() {
        hardwareScreen.isToolbarTitleValid(activityRule.getString(R.string.hardware))
    }

    @Test
    fun checkCpuTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.cpu))
        hardwareScreen.hasTextOnPosition("ABI", 0)
    }

    @Test
    fun checkGpuTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.gpu))
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.gles_version), 0)
    }

    @Test
    fun checkRamTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.ram))
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.total_memory), 0)
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.available_memory), 1)
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.threshold), 2)
    }

    @Test
    fun checkStorageTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.storage))
        hardwareScreen.hasAtLeastRvElements(2)
    }

    @Test
    fun checkScreenTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.screen))
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.screen_class), 0)
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.density_class), 1)
    }

    @Test
    fun checkAndroidTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.android))
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.version), 0)
        hardwareScreen.hasTextOnPosition("SDK", 1)
    }

    @Test
    fun checkHardwareTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.hardware))
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.battery), 0)
        hardwareScreen.hasTextOnPosition(activityRule.getString(R.string.level), 1)
    }

    @Test
    fun checkSensorsTab() {
        hardwareScreen.tapTabWithTitle(activityRule.getString(R.string.sensors))
        hardwareScreen.hasAtLeastRvElements(1)
    }
}