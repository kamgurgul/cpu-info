package com.kgurgul.cpuinfo.screens

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.CustomMatchers.withToolbarTitle
import com.kgurgul.cpuinfo.core.isk


/**
 * Screen with [com.kgurgul.cpuinfo.features.HostActivity] matchers
 *
 * @author kgurgul
 */
class HostScreen {

    private val drawerLayout = withId(R.id.drawer_layout)
    private val toolbar = withId(R.id.toolbar)
    private val navigationView = withId(R.id.navigation_view)

    /**
     * Check if toolbar title is equal with [title] param
     */
    fun isToolbarTitleValid(title: String) {
        onView(toolbar).check(matches(withToolbarTitle(isk(title))))
    }
}