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

package com.kgurgul.cpuinfo.utils.preferences

import android.content.SharedPreferences
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple wrapper for [SharedPreferences] which can also serialize and deserialize object from JSON
 */
@Singleton
class Prefs @Inject constructor(private val sharedPreferences: SharedPreferences) : IPrefs {

    /**
     * Verify if passed [key] is already saved in [SharedPreferences]
     */
    override fun contains(key: String): Boolean =
        sharedPreferences.contains(key)

    /**
     * Insert passed [value] with specific [key] into [SharedPreferences]. If [value] isn't a known
     * type it will be parsed into JSON.
     */
    override fun insert(key: String, value: Any) {
        when (value) {
            is Int -> sharedPreferences.edit().putInt(key, value).apply()
            is Float -> sharedPreferences.edit().putFloat(key, value).apply()
            is String -> sharedPreferences.edit().putString(key, value).apply()
            is Boolean -> sharedPreferences.edit().putBoolean(key, value).apply()
            is Long -> sharedPreferences.edit().putLong(key, value).apply()
            else -> {
                val s = Gson().toJson(value)
                sharedPreferences.edit().putString(key, s).apply()
            }
        }
    }

    /**
     * Get value with specific [key] stored in [SharedPreferences] and cast it into [T] type from
     * default value.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String, default: T): T {
        when (default) {
            is Int -> return sharedPreferences.getInt(key, default) as T
            is Float -> return sharedPreferences.getFloat(key, default) as T
            is String -> return sharedPreferences.getString(key, default) as T
            is Boolean -> return sharedPreferences.getBoolean(key, default) as T
            is Long -> return sharedPreferences.getLong(key, default) as T
            else -> {
                val value = sharedPreferences.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    val typedObject = default as Any
                    return Gson().fromJson(value, typedObject.javaClass) as T
                }
                return default
            }
        }
    }

    /**
     * Remove [key] from [SharedPreferences]
     */
    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}

