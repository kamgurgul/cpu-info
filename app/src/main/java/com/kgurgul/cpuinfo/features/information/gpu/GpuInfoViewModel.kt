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

package com.kgurgul.cpuinfo.features.information.gpu

import android.app.ActivityManager
import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.AdapterArrayList
import javax.inject.Inject

/**
 * ViewModel for GPU information. It is using custom SurfaceView to get more GPU details from OpenGL
 *
 * @author kgurgul
 */
class GpuInfoViewModel @Inject constructor(private val activityManager: ActivityManager,
                                           private val resources: Resources) : ViewModel() {

    val dataObservableList = AdapterArrayList<Pair<String, String>>()

    init {
        getGpuData()
    }

    /**
     * Get all GPU information
     */
    private fun getGpuData() {
        if (dataObservableList.isEmpty()) {
            val configurationInfo = activityManager.deviceConfigurationInfo
            val version = configurationInfo.glEsVersion
            if (!version.isNullOrEmpty()) {
                // Add GLES version on the first position because this ViewModel doesn't contains
                // synchronization with "addGlInfo" method
                dataObservableList.add(0, Pair(resources.getString(R.string.gles_version),
                        version))
            }
        }
    }

    /**
     * Check if additional GPU info was already added into [dataObservableList]
     */
    fun isGlInfoStored(): Boolean {
        return dataObservableList.size > 1
    }

    /**
     * Add additional GPU info from OpenGL if it wasn't added previously
     *
     * @param gpuInfoList list of additional data like version, render etc.
     */
    fun addGlInfo(gpuInfoList: List<Pair<String, String>>) {
        if (dataObservableList.size <= 1) {
            dataObservableList.addAll(gpuInfoList)
        }
    }
}