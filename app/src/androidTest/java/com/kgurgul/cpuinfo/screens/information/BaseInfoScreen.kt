package com.kgurgul.cpuinfo.screens.information

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.CustomMatchers.atPosition
import org.hamcrest.CoreMatchers.allOf

/**
 * Screen with [com.kgurgul.cpuinfo.features.information.base.BaseRvFragment] matchers
 *
 * @author kgurgul
 */
class BaseInfoScreen {

    private val recyclerView = withId(R.id.recycler_view)

    /**
     * Check if [RecyclerView] item has [text] at given [position]
     */
    fun hasTextOnPosition(text: String, position: Int) {
        onView(allOf(isDisplayed(), recyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(matches(atPosition(position, hasDescendant(withText(text)))))
    }
}