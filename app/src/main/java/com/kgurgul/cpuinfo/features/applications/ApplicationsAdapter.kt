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

package com.kgurgul.cpuinfo.features.applications

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.glide.GlideApp
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import com.kgurgul.cpuinfo.widgets.swiperv.SwipeHorizontalMenuLayout
import java.io.File


/**
 * Adapter for application list with sliding items
 *
 * @author kgurgul
 */
class ApplicationsAdapter(private val context: Context,
                          private val appList: List<ExtendedAppInfo>,
                          private val appClickListener: ItemClickListener)
    : RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder>() {

    private val packageManager: PackageManager = context.packageManager
    private val storageLabel: String = context.getString(R.string.storage_used)
    private val calculatingLabel: String = context.getString(R.string.calculating)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_swipe_app, parent, false)
        return ApplicationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val app = appList[position]

        GlideApp.with(context)
                .load(getApplicationInfo(app.sourceDir))
                .transition(DrawableTransitionOptions.withCrossFade())
                .fitCenter()
                .into(holder.iconIv)

        holder.nameTv.text = app.name
        holder.packageTv.text = app.packageName

        runOnApiBelow(Build.VERSION_CODES.O, {
            val size =
                    if (app.appSize == 0L) calculatingLabel
                    else Utils.humanReadableByteCount(app.appSize)
            holder.storageTv.text = "$storageLabel $size"
        }, {
            holder.storageTv.visibility = View.GONE
        })

        holder.mainContainer.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                appClickListener.appOpenClicked(pos)
            }
        }
        holder.settingsV.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                appClickListener.appSettingsClicked(holder.adapterPosition)
            }
        }
        holder.deleteView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                holder.sml.smoothCloseMenu()
                appClickListener.appUninstallClicked(holder.adapterPosition)
            }
        }

        // Native libs
        if (app.nativeLibraryDir != null) {
            val fileDir = File(app.nativeLibraryDir)
            val list = fileDir.listFiles()

            if (list != null && list.isNotEmpty()) {
                holder.nativeButtonIV.visibility = View.VISIBLE
                holder.nativeButtonIV.setOnClickListener {
                    appClickListener.appNativeLibsClicked(fileDir)
                }
            } else {
                holder.nativeButtonIV.visibility = View.GONE
            }
        } else {
            holder.nativeButtonIV.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = appList.size

    class ApplicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv: ImageView = itemView.findViewById(R.id.app_icon)
        val nameTv: TextView = itemView.findViewById(R.id.app_name)
        val packageTv: TextView = itemView.findViewById(R.id.app_package)
        val storageTv: TextView = itemView.findViewById(R.id.storage_usage)
        val sml: SwipeHorizontalMenuLayout = itemView.findViewById(R.id.sml)
        val mainContainer: View = itemView.findViewById(R.id.smContentView)
        val settingsV: View = itemView.findViewById(R.id.settings)
        val deleteView: View = itemView.findViewById(R.id.delete)
        val nativeButtonIV: ImageView = itemView.findViewById(R.id.native_button)
    }

    /**
     * Generate [ApplicationInfo] from [PackageInfo]
     */
    private fun getApplicationInfo(path: String): ApplicationInfo? {
        val info = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
        var appInfo: ApplicationInfo? = null
        if (info != null) {
            appInfo = info.applicationInfo
            appInfo!!.sourceDir = path
            appInfo.publicSourceDir = path
        }
        return appInfo
    }

    /**
     * Interface for communication with activity
     */
    interface ItemClickListener {
        fun appOpenClicked(position: Int)
        fun appSettingsClicked(position: Int)
        fun appUninstallClicked(position: Int)
        fun appNativeLibsClicked(nativeFile: File)
    }
}