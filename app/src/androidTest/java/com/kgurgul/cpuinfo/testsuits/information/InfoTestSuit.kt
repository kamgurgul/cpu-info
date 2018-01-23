package com.kgurgul.cpuinfo.testsuits.information

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.getString
import com.kgurgul.cpuinfo.features.HostActivity
import com.kgurgul.cpuinfo.screens.HostScreen
import com.kgurgul.cpuinfo.screens.information.BaseInfoScreen
import com.kgurgul.cpuinfo.screens.information.InfoContainerScreen
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

    private val hostScreen = HostScreen()
    private val infoContainerScreen = InfoContainerScreen()
    private val baseInfoScreen = BaseInfoScreen()

    @Test
    fun checkToolbarTitle() {
        hostScreen.isToolbarTitleValid(activityRule.getString(R.string.hardware))
    }

    @Test
    fun checkCpuTab() {
        infoContainerScreen.tapTabWithTitle(activityRule.getString(R.string.cpu))
        baseInfoScreen.hasTextOnPosition("ABI", 0)
    }
}