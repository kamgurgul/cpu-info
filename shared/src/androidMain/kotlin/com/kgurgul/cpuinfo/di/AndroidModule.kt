package com.kgurgul.cpuinfo.di

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.os.storage.StorageManager
import android.view.WindowManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@SuppressLint("WifiManagerLeak")
val androidModule = module {
    single { androidContext().resources }
    single { androidContext().packageManager }
    single { androidContext().contentResolver }
    single { androidContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }
    single {
        androidContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    single { androidContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    single { androidContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    single { androidContext().getSystemService(Context.WIFI_SERVICE) as WifiManager }
    single { androidContext().getSystemService(Context.STORAGE_SERVICE) as StorageManager }
    single { androidContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    single {
        PreferenceDataStoreFactory.create(
            produceFile = {
                androidContext().preferencesDataStoreFile(USER_PREFERENCES_NAME)
            }
        )
    }
}
