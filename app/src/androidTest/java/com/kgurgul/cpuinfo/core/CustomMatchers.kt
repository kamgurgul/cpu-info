package com.kgurgul.cpuinfo.core

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher


/**
 * Singleton with custom matchers.
 *
 * @author kgurgul
 */
object CustomMatchers {

    /**
     * Matcher for toolbar title
     */
    fun withToolbarTitle(textMatcher: Matcher<CharSequence>): Matcher<Any> {
        checkNotNull(textMatcher)
        return object : BoundedMatcher<Any, Toolbar>(Toolbar::class.java) {

            public override fun matchesSafely(toolbar: Toolbar): Boolean {
                return textMatcher.matches(toolbar.title)
            }

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                textMatcher.describeTo(description)
            }
        }
    }

    /**
     * Matcher for [RecyclerView] items
     */
    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}