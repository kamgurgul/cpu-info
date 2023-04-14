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

package com.kgurgul.cpuinfo.features.information.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kgurgul.cpuinfo.R

/**
 * Adapter for all info items inside [BaseRvFragment]. It should support two types of layouts:
 * vertical and horizontal. It also change color of the first string from passed [Pair] to red if
 * the second one is empty.
 *
 * @author kgurgul
 */
class InfoItemsAdapter(
    private val itemsObservableList: List<Pair<String, String>>,
    private val layoutType: LayoutType = InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT,
    private val onClickListener: OnClickListener? = null
) : RecyclerView.Adapter<InfoItemsAdapter.SingleItemViewHolder>() {

    enum class LayoutType {
        HORIZONTAL_LAYOUT, VERTICAL_LAYOUT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleItemViewHolder {
        val layout = if (layoutType == LayoutType.HORIZONTAL_LAYOUT)
            R.layout.item_value else R.layout.item_value_vertical
        return SingleItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layout, parent,
                false
            ), onClickListener
        )
    }

    override fun onBindViewHolder(holder: SingleItemViewHolder, position: Int) {
        holder.bind(itemsObservableList[position])
    }

    override fun getItemCount(): Int = itemsObservableList.size

    class SingleItemViewHolder(
        itemView: View,
        onClickListener: OnClickListener?
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleTv: TextView = itemView.findViewById(R.id.title_tv)
        private val valueTv: TextView = itemView.findViewById(R.id.value_tv)
        private var item: Pair<String, String>? = null

        init {
            itemView.findViewById<LinearLayout>(R.id.item_container).setOnLongClickListener { _ ->
                item?.let {
                    if (it.second.isNotEmpty()) {
                        onClickListener?.onItemLongPressed(it)
                        return@setOnLongClickListener true
                    }
                }
                false
            }
        }

        fun bind(item: Pair<String, String>) {
            this.item = item
            titleTv.text = item.first
            valueTv.text = item.second

            if (item.second.isEmpty()) {
                titleTv.setTextColor(ContextCompat.getColor(titleTv.context, R.color.accent))
                valueTv.visibility = View.GONE
            } else {
                titleTv.setTextColor(
                    ContextCompat.getColor(
                        titleTv.context,
                        R.color.onSurface
                    )
                )
                valueTv.visibility = View.VISIBLE
            }
        }
    }

    interface OnClickListener {

        /**
         * Invoked when user use long press on item
         */
        fun onItemLongPressed(item: Pair<String, String>)
    }
}