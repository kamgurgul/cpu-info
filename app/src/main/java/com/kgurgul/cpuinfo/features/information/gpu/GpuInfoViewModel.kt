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
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import javax.inject.Inject

/**
 * ViewModel for GPU information. It is using custom SurfaceView to get more GPU details from OpenGL
 *
 * @author kgurgul
 */
class GpuInfoViewModel @Inject constructor(
        private val activityManager: ActivityManager,
        private val resources: Resources) : ViewModel() {

    val listLiveData = ListLiveData<Pair<String, String>>()

    init {
        getGpuData()
    }

    /**
     * Get all GPU information
     */
    private fun getGpuData() {
        if (listLiveData.isEmpty()) {
            val configurationInfo = activityManager.deviceConfigurationInfo
            val version = configurationInfo.glEsVersion
            if (!version.isNullOrEmpty()) {
                // Add GLES version on the first position because this ViewModel doesn't contains
                // synchronization with "addGlInfo" method
                listLiveData.add(0, Pair(resources.getString(R.string.gles_version),
                        version))
            }
        }
    }

    /**
     * Check if additional GPU info was already added into [listLiveData]
     */
    fun isGlInfoStored(): Boolean = listLiveData.size > 1

    /**
     * Add additional GPU info from OpenGL if it wasn't added previously
     *
     * @param gpuInfoMap map of additional data like version, render etc.
     */
    fun addGlInfo(gpuInfoMap: Map<GlInfoType, String?>) {
        if (listLiveData.size <= 1) {
            val gpuInfoPairs = ArrayList<Pair<String, String>>()
            Utils.addPairIfExists(gpuInfoPairs, resources.getString(R.string.vendor),
                    gpuInfoMap[GlInfoType.GL_VENDOR])
            Utils.addPairIfExists(gpuInfoPairs, resources.getString(R.string.version),
                    gpuInfoMap[GlInfoType.GL_VERSION])
            Utils.addPairIfExists(gpuInfoPairs, resources.getString(R.string.renderer),
                    gpuInfoMap[GlInfoType.GL_RENDERER])
            Utils.addPairIfExists(gpuInfoPairs, resources.getString(R.string.extensions),
                    gpuInfoMap[GlInfoType.GL_EXTENSIONS])
            listLiveData.addAll(gpuInfoPairs)
        }
    }

    enum class GlInfoType {
        GL_VENDOR, GL_VERSION, GL_RENDERER, GL_EXTENSIONS
    }
}