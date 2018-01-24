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

package com.kgurgul.cpuinfo.screens

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.CustomMatchers.atPosition
import com.kgurgul.cpuinfo.core.CustomMatchers.withToolbarTitle
import com.kgurgul.cpuinfo.core.RecyclerViewItemCountAssertion
import com.kgurgul.cpuinfo.core.isk
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers.greaterThanOrEqualTo

/**
 * Class with matchers for [com.kgurgul.cpuinfo.features.HostActivity],
 * [com.kgurgul.cpuinfo.features.information.InfoContainerFragment] and
 * [com.kgurgul.cpuinfo.features.information.base.BaseRvFragment]
 *
 * @author kgurgul
 */
class HardwareScreen {

    private val toolbar = withId(R.id.toolbar)
    private val tabLayout = withId(R.id.tabs)
    private val recyclerView = withId(R.id.recycler_view)

    /**
     * Check if toolbar title is equal with [title] param
     */
    fun isToolbarTitleValid(title: String) {
        onView(toolbar).check(matches(withToolbarTitle(isk(title))))
    }

    /**
     * Open tab with specific title
     */
    fun tapTabWithTitle(title: String) {
        onView(allOf<View>(withParent(isDescendantOfA(tabLayout)), withText(title)))
                .perform(scrollTo(), click())
    }

    /**
     * Check if [RecyclerView] item has [text] at given [position]
     */
    fun hasTextOnPosition(text: String, position: Int) {
        onView(allOf(isDisplayed(), recyclerView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
                .check(matches(atPosition(position, hasDescendant(withText(text)))))
    }

    fun hasAtLeastRvElements(amount: Int) {
        onView(allOf(isDisplayed(), recyclerView))
                .check(RecyclerViewItemCountAssertion(greaterThanOrEqualTo(amount)))
    }
}