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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.BaseTestSuit
import com.kgurgul.cpuinfo.core.getString
import com.kgurgul.cpuinfo.pages.InfoPage
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InfoTestSuit : BaseTestSuit() {

    private val infoPage = InfoPage()

    @Test
    fun checkToolbarTitle() {
        infoPage.isToolbarTitleValid(getString(R.string.hardware))
    }

    @Test
    fun checkCpuTab() {
        infoPage.tapTabWithTitle(getString(R.string.cpu))
            .assertHasAnyElements()
    }

    @Test
    fun checkGpuTab() {
        infoPage.tapTabWithTitle(getString(R.string.gpu))
            .assertHasAnyElements()
    }

    @Test
    fun checkRamTab() {
        infoPage.tapTabWithTitle(getString(R.string.ram))
            .assertTextOnPosition(getString(R.string.total_memory), 0)
            .assertTextOnPosition(getString(R.string.available_memory), 1)
            .assertTextOnPosition(getString(R.string.threshold), 2)
    }

    @Test
    fun checkStorageTab() {
        infoPage.tapTabWithTitle(getString(R.string.storage))
            .assertHasAnyElements()
    }

    @Test
    fun checkScreenTab() {
        infoPage.tapTabWithTitle(getString(R.string.screen))
            .assertTextOnPosition(getString(R.string.screen_class), 0)
            .assertTextOnPosition(getString(R.string.density_class), 1)
    }

    @Test
    fun checkAndroidTab() {
        infoPage.tapTabWithTitle(getString(R.string.android))
            .assertTextOnPosition(getString(R.string.version), 0)
            .assertTextOnPosition("SDK", 1)
    }

    @Test
    fun checkHardwareTab() {
        infoPage.tapTabWithTitle(getString(R.string.hardware))
            .assertHasAnyElements()
    }

    @Test
    fun checkSensorsTab() {
        infoPage.tapTabWithTitle(getString(R.string.sensors))
            .assertHasAnyElements()
    }
}