package com.kgurgul.cpuinfo.uitestutils.conditionwatcher

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.kgurgul.cpuinfo.uitestutils.conditionwatcher.ConditionWatcher.waitForCondition
import org.hamcrest.Matcher
import org.hamcrest.StringDescription

/**
 * Waits for the view specified by the matcher meats the specified condition
 */
fun waitForView(viewMatcher: Matcher<View>) = ViewMatcherWaiter(viewMatcher)

/**
 * Used by the waitForView() shorthand fluent function
 */
class ViewMatcherWaiter constructor(val viewMatcher: Matcher<View>) {

    /**
     * Specify the Espresso matches which will satisfy the condition
     */
    fun toMatch(viewChecker: Matcher<View>) = waitForCondition(object : Instruction() {
        override val description: String
            get() {
                val desc = StringDescription()
                desc.appendText("Wait for view ")
                viewMatcher.describeTo(desc)
                desc.appendText(" to match ")
                viewChecker.describeTo(desc)
                return desc.toString()
            }

        override fun checkCondition(): Boolean {
            return try {
                onView(viewMatcher).check(matches(viewChecker))
                true
            } catch (e: Throwable) {
                false
            }
        }
    })
}