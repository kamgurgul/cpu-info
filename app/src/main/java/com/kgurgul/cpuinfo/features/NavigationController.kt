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

import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.analytics.AnalyticsEvents
import com.kgurgul.cpuinfo.analytics.AnalyticsManager
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
class NavigationController @Inject constructor(hostActivity: HostActivity,
                                               private val analyticsManager: AnalyticsManager) {

    companion object {
        val HOME_FRAGMENT_TAG = "HOME_FRAGMENT_TAG"
        val SECOND_FRAGMENT_TAG = "SECOND_FRAGMENT_TAG"
    }

    private val fragmentManager = hostActivity.supportFragmentManager
    private val containerId = R.id.fragment_container

    fun navigateToInfo() {
        analyticsManager.setScreenName(AnalyticsEvents.INFO_SCREEN)
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
        analyticsManager.setScreenName(AnalyticsEvents.TEMPERATURE_SCREEN)
        val temperatureFragment = TemperatureFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, temperatureFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateToApplications() {
        analyticsManager.setScreenName(AnalyticsEvents.APPLICATIONS_SCREEN)
        val applicationsFragment = ApplicationsFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, applicationsFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateToProcesses() {
        analyticsManager.setScreenName(AnalyticsEvents.PROCESSES_SCREEN)
        val processesFragment = ProcessesFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, processesFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateToSettings() {
        analyticsManager.setScreenName(AnalyticsEvents.SETTINGS_SCREEN)
        val settingsFragment = SettingsFragment()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, settingsFragment, SECOND_FRAGMENT_TAG)
        if (fragmentManager.backStackEntryCount == 0) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }
}