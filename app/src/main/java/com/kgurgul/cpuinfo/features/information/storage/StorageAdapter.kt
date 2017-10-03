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

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.SimpleListChangeNotifier
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.round2
import com.kgurgul.cpuinfo.widgets.progress.IconRoundCornerProgressBar
import java.lang.ref.WeakReference

/**
 * Adpter for items in [StorageInfoFragment]. It is copy from
 * [com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter] because storage has
 * another data container. It can be replaced with something more generic.
 *
 * @author kgurgul
 */
class StorageAdapter(private val storageObservableList: ObservableList<StorageItem>) :
        RecyclerView.Adapter<StorageAdapter.ViewHolder>(), LifecycleObserver {

    private val simpleListChangeNotifier =
            SimpleListChangeNotifier<StorageItem>(WeakReference(this))

    init {
        storageObservableList.addOnListChangedCallback(simpleListChangeNotifier)
    }

    override fun getItemCount(): Int {
        return storageObservableList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_storage, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(storageObservableList[position])
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        storageObservableList.removeOnListChangedCallback(simpleListChangeNotifier)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storageDescriptionTv: TextView = itemView.findViewById(R.id.storage_description_tv)
        private val storageProgress: IconRoundCornerProgressBar = itemView.findViewById(R.id.storage_progress)

        fun bindViewHolder(storageItem: StorageItem) {
            val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
            val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
            val usedPercent = (storageItem.storageUsed.toFloat()
                    / storageItem.storageTotal.toFloat() * 100.0).round2()

            val storageDesc = "${storageItem.type}: $usedReadable / $totalReadable ($usedPercent%)"
            storageDescriptionTv.text = storageDesc

            storageProgress.iconImageResource = storageItem.iconRes
            storageProgress.progress = usedPercent.toFloat()
        }
    }
}