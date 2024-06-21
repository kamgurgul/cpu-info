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

package com.kgurgul.cpuinfo.core

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsToggleable
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.withText

fun ViewInteraction.isDisplayed(): Boolean {
    return try {
        check(matches(ViewMatchers.isDisplayed()))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.isNotDisplayed(): Boolean {
    return !isDisplayed()
}

fun ViewInteraction.isEnabled(): Boolean {
    return try {
        check(matches(ViewMatchers.isEnabled()))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.isNotEnabled(): Boolean {
    return try {
        check(matches(ViewMatchers.isNotEnabled()))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.isChecked(): Boolean {
    return try {
        check(matches(ViewMatchers.isChecked()))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.isNotChecked(): Boolean {
    return try {
        check(matches(ViewMatchers.isNotChecked()))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.isClickable(): Boolean {
    return try {
        check(matches(ViewMatchers.isClickable()))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.hasVisibleText(text: String): Boolean {
    return try {
        check(matches(withText(text)))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.isEmpty(): Boolean {
    return try {
        check(matches(withText("")))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.hasAnyElement(): Boolean {
    return try {
        check(matches(hasMinimumChildCount(1)))
        true
    } catch (e: Throwable) {
        false
    }
}

fun ViewInteraction.hasElementsCount(count: Int): Boolean {
    return try {
        check(matches(hasChildCount(count)))
        true
    } catch (e: Throwable) {
        false
    }
}

fun SemanticsNodeInteraction.isToggleable(): Boolean {
    return try {
        assertIsToggleable()
        true
    } catch (e: Throwable) {
        false
    }
}

fun SemanticsNodeInteraction.isDisplayed(): Boolean {
    return try {
        assertIsDisplayed()
        true
    } catch (e: Throwable) {
        false
    }
}

fun SemanticsNodeInteraction.isNotDisplayed(): Boolean {
    return !isDisplayed()
}

fun SemanticsNodeInteractionCollection.hasAnyElement(): Boolean {
    val matchedNodes = fetchSemanticsNodes(
        atLeastOneRootRequired = true,
    )
    return matchedNodes.isNotEmpty()
}
