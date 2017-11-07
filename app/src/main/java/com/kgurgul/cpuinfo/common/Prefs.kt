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

package com.kgurgul.cpuinfo.common

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple wrapper for [SharedPreferences] which can also serialize and deserialize object from JSON
 */
@Singleton
class Prefs @Inject constructor(var app: Application) {

    fun contains(key: String): Boolean {
        return prefs().contains(key)
    }

    fun insert(key: String, value: Any) {
        when (value) {
            is Int -> prefs().edit().putInt(key, value).apply()
            is Float -> prefs().edit().putFloat(key, value).apply()
            is String -> prefs().edit().putString(key, value).apply()
            is Boolean -> prefs().edit().putBoolean(key, value).apply()
            is Long -> prefs().edit().putLong(key, value).apply()
            else -> {
                val s = Gson().toJson(value)
                prefs().edit().putString(key, s).apply()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T): T {
        when (default) {
            is Int -> return prefs().getInt(key, default) as T
            is Float -> return prefs().getFloat(key, default) as T
            is String -> return prefs().getString(key, default) as T
            is Boolean -> return prefs().getBoolean(key, default) as T
            is Long -> return prefs().getLong(key, default) as T
            else -> {
                val value = prefs().getString(key, "")
                if (value.isNotEmpty()) {
                    val typedObject = default as Any
                    return Gson().fromJson(value, typedObject.javaClass) as T
                }
                return default
            }
        }
    }

    fun remove(key: String) {
        prefs().edit().remove(key).apply()
    }

    fun prefs(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }
}

