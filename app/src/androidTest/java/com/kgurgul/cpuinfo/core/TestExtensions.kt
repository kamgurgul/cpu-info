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

@file:JvmName("TestExtensions")

package com.kgurgul.cpuinfo.core

import android.app.Activity
import androidx.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers

/**
 * Kotlin definition for [CoreMatchers. is] method to avoid problems with 'is' keyword
 */
fun <T> isk(value: T) = CoreMatchers.`is`(value)!!

/**
 * Helpers for getting string from [ActivityTestRule]. Throws exception in case of missing string.
 */
fun <T : Activity> ActivityTestRule<T>.getString(id: Int) = activity!!.resources.getString(id)!!