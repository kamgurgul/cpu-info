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
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.glide.GlideApp
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import com.kgurgul.cpuinfo.widgets.swiperv.SwipeHorizontalMenuLayout


/**
 * Adapter for application list with sliding items
 *
 * @author kgurgul
 */
class ApplicationsAdapter(
        private val appList: List<ExtendedAppInfo>,
        private val appClickListener: ItemClickListener
) : RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_swipe_app, parent, false)
        return ApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val app = appList[position]
        holder.bind(app, appClickListener)
    }

    override fun getItemCount(): Int = appList.size

    class ApplicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storageLabel: String = itemView.context.getString(R.string.storage_used)
        private val calculatingLabel: String = itemView.context.getString(R.string.calculating)

        private val iconIv: ImageView = itemView.findViewById(R.id.app_icon)
        private val nameTv: TextView = itemView.findViewById(R.id.app_name)
        private val packageTv: TextView = itemView.findViewById(R.id.app_package)
        private val storageTv: TextView = itemView.findViewById(R.id.storage_usage)
        private val sml: SwipeHorizontalMenuLayout = itemView.findViewById(R.id.sml)
        private val mainContainer: View = itemView.findViewById(R.id.smContentView)
        private val settingsV: View = itemView.findViewById(R.id.settings)
        private val deleteView: View = itemView.findViewById(R.id.delete)
        private val nativeButtonIV: ImageView = itemView.findViewById(R.id.native_button)

        @SuppressLint("SetTextI18n")
        fun bind(app: ExtendedAppInfo, appClickListener: ItemClickListener) {
            GlideApp.with(iconIv.context)
                    .load(app.appIconUri)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .fitCenter()
                    .into(iconIv)

            nameTv.text = app.name
            packageTv.text = app.packageName

            runOnApiBelow(Build.VERSION_CODES.O, {
                val size =
                        if (app.appSize == 0L) calculatingLabel
                        else Utils.humanReadableByteCount(app.appSize)
                storageTv.text = "$storageLabel $size"
            }, {
                storageTv.visibility = View.GONE
            })

            mainContainer.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    appClickListener.appOpenClicked(pos)
                }
            }
            settingsV.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    appClickListener.appSettingsClicked(bindingAdapterPosition)
                }
            }
            deleteView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    sml.smoothCloseMenu()
                    appClickListener.appUninstallClicked(bindingAdapterPosition)
                }
            }
            nativeButtonIV.setOnClickListener {
                app.nativeLibraryDir?.let { appClickListener.appNativeLibsClicked(it) }
            }
            if (app.hasNativeLibs) {
                nativeButtonIV.visibility = View.VISIBLE
            } else {
                nativeButtonIV.visibility = View.GONE
            }
        }
    }

    /**
     * Interface for communication with activity
     */
    interface ItemClickListener {
        fun appOpenClicked(position: Int)
        fun appSettingsClicked(position: Int)
        fun appUninstallClicked(position: Int)
        fun appNativeLibsClicked(nativeDir: String)
    }
}