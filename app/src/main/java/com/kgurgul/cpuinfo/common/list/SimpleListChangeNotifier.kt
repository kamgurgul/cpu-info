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

package com.kgurgul.cpuinfo.common.list

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * In case of any change on the list, this notifier will notify adapter about specific change
 *
 * @author kgurgul
 */
class SimpleListChangeNotifier<T>(val adapter: WeakReference<RecyclerView.Adapter<*>>) :
        ObservableList.OnListChangedCallback<ObservableList<T>>() {
    override fun onChanged(sender: ObservableList<T>) {
        adapter.get()?.notifyDataSetChanged()
    }

    override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapter.get()?.notifyItemRangeRemoved(positionStart, itemCount)
    }

    override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapter.get()?.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapter.get()?.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        adapter.get()?.notifyDataSetChanged()
    }
}
