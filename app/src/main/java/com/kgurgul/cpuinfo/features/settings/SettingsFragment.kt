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

package com.kgurgul.cpuinfo.features.settings

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.runOnApiAbove

/**
 * Provides possibility to change RAM widget refreshing time and temperature units
 *
 * @author kgurgul
 */
class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        const val KEY_RAM_REFRESHING = "ram_refreshing"

        private const val KEY_RAM_CATEGORIES = "pref_key_ram_settings"
    }

    private lateinit var temperatureUnitPreference: ListPreference
    private lateinit var ramRefreshingPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        temperatureUnitPreference = preferenceScreen.findPreference(KEY_TEMPERATURE_UNIT)
                as ListPreference
        ramRefreshingPreference = preferenceScreen.findPreference(KEY_RAM_REFRESHING)
                as ListPreference

        // RAM widget isn't supported currently on O and above
        runOnApiAbove(Build.VERSION_CODES.N_MR1) {
            preferenceScreen.removePreference(preferenceScreen.findPreference(KEY_RAM_CATEGORIES))
        }
    }

    override fun onResume() {
        super.onResume()

        temperatureUnitPreference.summary = temperatureUnitPreference.entry.toString()
        ramRefreshingPreference.summary = ramRefreshingPreference.entry.toString()

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()

        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            KEY_TEMPERATURE_UNIT ->
                temperatureUnitPreference.summary = temperatureUnitPreference.entry.toString()
            KEY_RAM_REFRESHING ->
                ramRefreshingPreference.summary = ramRefreshingPreference.entry.toString()
        }
    }
}