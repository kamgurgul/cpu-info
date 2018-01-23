@file:JvmName("TestExtensions")

package com.kgurgul.cpuinfo.core

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers

/**
 * Kotlin definition for [CoreMatchers. is] method to avoid problems with 'is' keyword
 */
fun <T> isk(value: T) = CoreMatchers.`is`(value)!!

/**
 * Helpers for getting string from [ActivityTestRule]. Throws exception in case of missing string.
 */
fun <T : Activity> ActivityTestRule<T>.getString(id: Int) = activity!!.resources.getString(id)!!