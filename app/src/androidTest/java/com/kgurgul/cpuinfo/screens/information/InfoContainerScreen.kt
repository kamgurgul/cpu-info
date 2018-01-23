package com.kgurgul.cpuinfo.screens.information

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.View
import com.kgurgul.cpuinfo.R
import org.hamcrest.CoreMatchers.allOf

/**
 * Screen with [com.kgurgul.cpuinfo.features.information.InfoContainerFragment] matchers
 *
 * @author kgurgul
 */
class InfoContainerScreen {

    private val viewPager = withId(R.id.view_pager)
    private val tabLayout = withId(R.id.tabs)

    /**
     * Open tab with specific title
     */
    fun tapTabWithTitle(title: String) {
        onView(allOf<View>(withParent(isDescendantOfA(tabLayout)), withText(title)))
                .perform(scrollTo(), click())
    }
}