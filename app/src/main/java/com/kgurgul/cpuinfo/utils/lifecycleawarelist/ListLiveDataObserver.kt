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

package com.kgurgul.cpuinfo.utils.lifecycleawarelist

import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView

/**
 * [android.arch.lifecycle.LiveData] observer for [ListLiveData] which will notify [adapter] about
 * list changes.
 *
 * @author kgurgul
 */
class ListLiveDataObserver(val adapter: RecyclerView.Adapter<*>)
    : Observer<ListLiveDataChangeEvent> {

    override fun onChanged(changeEvent: ListLiveDataChangeEvent?) {
        changeEvent?.let {
            when (it.listLiveDataState) {
                ListLiveDataState.CHANGED ->
                    adapter.notifyDataSetChanged()
                ListLiveDataState.ITEM_RANGE_CHANGED ->
                    adapter.notifyItemRangeChanged(it.startIndex, it.itemCount)
                ListLiveDataState.ITEM_RANGE_INSERTED ->
                    adapter.notifyItemRangeInserted(it.startIndex, it.itemCount)
                ListLiveDataState.ITEM_RANGE_REMOVED ->
                    adapter.notifyItemRangeRemoved(it.startIndex, it.itemCount)
            }
        }
    }
}