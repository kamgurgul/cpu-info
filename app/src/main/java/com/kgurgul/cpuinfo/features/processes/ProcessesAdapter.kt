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

package com.kgurgul.cpuinfo.features.processes

import android.annotation.SuppressLint
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
import java.lang.ref.WeakReference

/**
 * Simple adapter for processes
 *
 * @author kgurgul
 */
class ProcessesAdapter(private val processList: ObservableList<ProcessItem>)
    : RecyclerView.Adapter<ProcessesAdapter.ViewHolder>(), LifecycleObserver {

    private val simpleListChangeNotifier =
            SimpleListChangeNotifier<ProcessItem>(WeakReference(this))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_process, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val process = processList[position]
        holder.nameTv.text = process.name
        holder.pidTv.text = "PID: ${process.pid}"
        holder.ppidTv.text = "PPID: ${process.ppid}"
        holder.nicenessTv.text = "NICENESS: ${process.niceness}"
        holder.userTv.text = "USER: ${process.user}"
        holder.rssTv.text = "RSS: ${Utils.humanReadableByteCount(process.rss.toLong() * 1024)}"
        holder.vsizeTv.text = "VSZ: ${Utils.humanReadableByteCount(process.vsize.toLong() * 1024)}"
    }

    override fun getItemCount(): Int {
        return processList.size
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        processList.addOnListChangedCallback(simpleListChangeNotifier)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        processList.removeOnListChangedCallback(simpleListChangeNotifier)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.proc_name_tv)
        val pidTv: TextView = itemView.findViewById(R.id.proc_pid_tv)
        val ppidTv: TextView = itemView.findViewById(R.id.proc_ppid_tv)
        val nicenessTv: TextView = itemView.findViewById(R.id.proc_nic_tv)
        val userTv: TextView = itemView.findViewById(R.id.proc_user_tv)
        val rssTv: TextView = itemView.findViewById(R.id.proc_rss_tv)
        val vsizeTv: TextView = itemView.findViewById(R.id.proc_vsize_tv)
    }
}