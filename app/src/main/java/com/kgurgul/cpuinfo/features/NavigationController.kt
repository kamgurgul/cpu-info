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

    companion object {
        val HOME_FRAGMENT_TAG = "HOME_FRAGMENT_TAG"
        val SECOND_FRAGMENT_TAG = "SECOND_FRAGMENT_TAG"
    }

    private val fragmentManager = hostActivity.supportFragmentManager
    private val containerId = R.id.fragment_container

    fun navigateToInfo() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.INFO_SCREEN, null)
        val infoFragment = ContainerInfoFragment()
        if (fragmentManager.findFragmentByTag(HOME_FRAGMENT_TAG) != null) {
            val currentFragment = fragmentManager.findFragmentByTag(SECOND_FRAGMENT_TAG)
            if (currentFragment != null) {
                fragmentManager.beginTransaction().remove(currentFragment).commitNow()
            }
            fragmentManager.popBackStackImmediate()
        } else {
            fragmentManager.beginTransaction()
                    .replace(containerId, infoFragment, HOME_FRAGMENT_TAG)
                    .commitAllowingStateLoss()
        }
    }

    fun navigateToTemperature() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.TEMPERATURE_SCREEN, null)
        val temperatureFragment = TemperatureFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, temperatureFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateToApplications() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.APPLICATIONS_SCREEN, null)
        val applicationsFragment = ApplicationsFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, applicationsFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateToProcesses() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.PROCESSES_SCREEN, null)
        val processesFragment = ProcessesFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, processesFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateToSettings() {
        firebaseAnalytics.setCurrentScreen(hostActivity, AnalyticsEvents.SETTINGS_SCREEN, null)
        val settingsFragment = SettingsFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, settingsFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }
}