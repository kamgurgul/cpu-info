/*
 * Copyright 2017 KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kgurgul.cpuinfo.features

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.google.firebase.analytics.FirebaseAnalytics
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.analytics.AnalyticsEvents
import com.kgurgul.cpuinfo.features.applications.ApplicationsFragment
import com.kgurgul.cpuinfo.features.information.ContainerInfoFragment
import com.kgurgul.cpuinfo.features.processes.ProcessesFragment
import com.kgurgul.cpuinfo.features.settings.SettingsFragment
import com.kgurgul.cpuinfo.features.temperature.TemperatureFragment
import javax.inject.Inject

/**
 * NavigationController controls whole navigation in [HostActivity]
 *
 * @author kgurgul
 */
class NavigationController @Inject constructor(private val hostActivity: HostActivity,
                                               private val firebaseAnalytics: FirebaseAnalytics) {

    enum class FragmentTag(val tag: String) {
        PARENT_FRAGMENT_TAG("PARENT_FRAGMENT_TAG"),
        SINGLE_CHILD_FRAGMENT_TAG("SINGLE_CHILD_FRAGMENT_TAG")
    }

    private val fragmentManager = hostActivity.supportFragmentManager
    private val containerId = R.id.fragment_container

    fun navigateToInfo() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.INFO_SCREEN, null)
        val infoFragment = ContainerInfoFragment()
        addToSingleChildStack(infoFragment, FragmentTag.PARENT_FRAGMENT_TAG)
    }

    fun navigateToTemperature() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.TEMPERATURE_SCREEN, null)
        val temperatureFragment = TemperatureFragment()
        addToSingleChildStack(temperatureFragment, FragmentTag.SINGLE_CHILD_FRAGMENT_TAG)
    }

    fun navigateToApplications() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.APPLICATIONS_SCREEN, null)
        val applicationsFragment = ApplicationsFragment()
        addToSingleChildStack(applicationsFragment, FragmentTag.SINGLE_CHILD_FRAGMENT_TAG)
    }

    fun navigateToProcesses() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.PROCESSES_SCREEN, null)
        val processesFragment = ProcessesFragment()
        addToSingleChildStack(processesFragment, FragmentTag.SINGLE_CHILD_FRAGMENT_TAG)
    }

    fun navigateToSettings() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.SETTINGS_SCREEN, null)
        val settingsFragment = SettingsFragment()
        addToSingleChildStack(settingsFragment, FragmentTag.SINGLE_CHILD_FRAGMENT_TAG)
    }

    /**
     * App navigation will currently build stack with [ContainerInfoFragment] on the top and all
     * others fragments as a single child.
     */
    private fun addToSingleChildStack(fragment: Fragment, fragmentTag: FragmentTag) {
        if (fragmentTag == FragmentTag.PARENT_FRAGMENT_TAG) {
            if (fragmentManager.findFragmentByTag(FragmentTag.PARENT_FRAGMENT_TAG.tag) != null) {
                val currentFragment =
                        fragmentManager.findFragmentByTag(FragmentTag.SINGLE_CHILD_FRAGMENT_TAG.tag)
                if (currentFragment != null) {
                    fragmentManager.beginTransaction()
                            .remove(currentFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commitNow()
                }
                fragmentManager.popBackStackImmediate()
            } else {
                fragmentManager.beginTransaction()
                        .replace(containerId, fragment, fragmentTag.tag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commitAllowingStateLoss()
            }
        } else {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val hasOnlyParentFragment = fragmentManager.findFragmentByTag(
                    FragmentTag.SINGLE_CHILD_FRAGMENT_TAG.tag) == null
            fragmentTransaction.replace(containerId, fragment, fragmentTag.tag)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            if (hasOnlyParentFragment) {
                fragmentTransaction.addToBackStack(null)
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
}