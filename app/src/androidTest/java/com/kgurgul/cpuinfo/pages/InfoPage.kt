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

package com.kgurgul.cpuinfo.pages

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.core.CustomMatchers.atPosition
import com.kgurgul.cpuinfo.core.CustomMatchers.withToolbarTitle
import com.kgurgul.cpuinfo.core.hasAnyElement
import com.kgurgul.cpuinfo.core.isk
import com.kgurgul.cpuinfo.uitestutils.conditionwatcher.waitForCondition
import org.hamcrest.CoreMatchers.allOf

class InfoPage {

    private val toolbar = withId(R.id.toolbar)
    private val tabLayout = withId(R.id.tabs)
    private val recyclerView = withId(R.id.recycler_view)

    /**
     * Check if toolbar title is equal with [title] param
     */
    fun isToolbarTitleValid(title: String): InfoPage {
        onView(toolbar).check(matches(withToolbarTitle(isk(title))))
        return this
    }

    /**
     * Open tab with specific title
     */
    fun tapTabWithTitle(title: String): InfoPage {
        onView(allOf(withParent(isDescendantOfA(tabLayout)), withText(title)))
            .perform(scrollTo(), click())
        return this
    }

    /**
     * Check if [RecyclerView] item has [text] at given [position]
     */
    fun hasTextOnPosition(text: String, position: Int) {
        onView(allOf(isDisplayed(), recyclerView))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(position))
            .check(matches(atPosition(position, hasDescendant(withText(text)))))
    }

    fun assertHasAnyElements(): InfoPage {
        waitForCondition { onView(recyclerView).hasAnyElement() }
        return this
    }
}