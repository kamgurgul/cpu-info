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

package com.kgurgul.cpuinfo.features.information.storage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.viewModels
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import dagger.hilt.android.AndroidEntryPoint

/**
 * Displays info about used and free storage on the device
 *
 * @author kgurgul
 */
@AndroidEntryPoint
class StorageInfoFragment : BaseRvFragment() {

    private var receiverRegistered = false

    private val handler = Handler(Looper.getMainLooper())
    private val mountedReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ viewModel.refreshSdCard() }, 2000)
        }
    }

    private val viewModel: StorageInfoViewModel by viewModels()

    override fun onResume() {
        super.onResume()

        // Register events connected with inserting SD card
        if (!receiverRegistered) {
            receiverRegistered = true
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
            filter.addAction(Intent.ACTION_MEDIA_CHECKING)
            filter.addAction(Intent.ACTION_MEDIA_EJECT)
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED)
            filter.addAction(Intent.ACTION_MEDIA_NOFS)
            filter.addAction(Intent.ACTION_MEDIA_REMOVED)
            filter.addAction(Intent.ACTION_MEDIA_SHARED)
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE)
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED)
            filter.addDataScheme("file")
            requireActivity().registerReceiver(mountedReceiver, filter)
        }
    }

    override fun onPause() {
        if (receiverRegistered) {
            receiverRegistered = false
            requireActivity().unregisterReceiver(mountedReceiver)
            handler.removeCallbacksAndMessages(null)
        }

        super.onPause()
    }

    override fun setupRecyclerViewAdapter() {
        val storageAdapter = StorageAdapter(viewModel.listLiveData)
        viewModel.listLiveData.listStatusChangeNotificator.observe(viewLifecycleOwner,
                ListLiveDataObserver(storageAdapter))
        recyclerView.adapter = storageAdapter
    }
}