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

package com.kgurgul.cpuinfo.di.modules

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.storage.StorageManager
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module which can provide all singletons
 *
 * @author kgurgul
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext appContext: Context): Resources =
            appContext.resources

    @Provides
    @Singleton
    fun provideActivityManager(@ApplicationContext appContext: Context): ActivityManager =
            appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    @Provides
    @Singleton
    fun provideDevicePolicyManager(@ApplicationContext appContext: Context): DevicePolicyManager =
            appContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext appContext: Context): PackageManager =
            appContext.packageManager

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext appContext: Context): ContentResolver =
            appContext.contentResolver

    @Provides
    @Singleton
    fun provideWindowManager(@ApplicationContext appContext: Context): WindowManager =
            appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext appContext: Context): SensorManager =
            appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    @SuppressLint("WifiManagerPotentialLeak")
    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext appContext: Context): WifiManager =
            appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(appContext)

    @Provides
    @Singleton
    fun provideStorageManager(@ApplicationContext appContext: Context): StorageManager =
        appContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile(USER_PREFERENCES_NAME)
            }
        )

    companion object {
        const val USER_PREFERENCES_NAME = "user_preferences"
    }
}