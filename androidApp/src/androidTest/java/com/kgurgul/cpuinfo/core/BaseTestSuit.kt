package com.kgurgul.cpuinfo.core

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kgurgul.cpuinfo.features.HostActivity
import org.junit.Rule

abstract class BaseTestSuit {

    @get:Rule
    val androidComposeRule = createAndroidComposeRule<HostActivity>()
}