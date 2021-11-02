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

import androidx.lifecycle.MutableLiveData

/**
 * [ArrayList] with additional [listStatusChangeNotificator] which will notify all observers about
 * changes in that list. All operations MUST be invoked from main thread to avoid inconsistency.
 *
 * In addition to normal [ArrayList] it contains [replace] method to make nicer animations with
 * [androidx.recyclerview.widget.RecyclerView].
 *
 * @author kgurgul
 */
class ListLiveData<T> : ArrayList<T>() {

    val listStatusChangeNotificator = MutableLiveData<ListLiveDataChangeEvent>()

    override fun add(element: T): Boolean {
        super.add(element)
        listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                listLiveDataState = ListLiveDataState.ITEM_RANGE_INSERTED,
                startIndex = size - 1,
                itemCount = 1
        )
        return true
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                listLiveDataState = ListLiveDataState.ITEM_RANGE_INSERTED,
                startIndex = index,
                itemCount = 1
        )
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val oldSize = size
        val added = super.addAll(elements)
        if (added) {
            listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                    listLiveDataState = ListLiveDataState.ITEM_RANGE_INSERTED,
                    startIndex = oldSize,
                    itemCount = size - oldSize
            )
        }
        return added
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val added = super.addAll(index, elements)
        if (added) {
            listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                    listLiveDataState = ListLiveDataState.ITEM_RANGE_INSERTED,
                    startIndex = index,
                    itemCount = elements.size
            )
        }
        return added
    }

    override fun clear() {
        val oldSize = size
        super.clear()
        if (oldSize != 0) {
            listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                    listLiveDataState = ListLiveDataState.ITEM_RANGE_REMOVED,
                    startIndex = 0,
                    itemCount = oldSize
            )
        }
    }

    override fun removeAt(index: Int): T {
        val value = super.removeAt(index)
        listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                listLiveDataState = ListLiveDataState.ITEM_RANGE_REMOVED,
                startIndex = index,
                itemCount = 1
        )
        return value
    }

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        return if (index >= 0) {
            removeAt(index)
            true
        } else {
            false
        }
    }

    override fun set(index: Int, element: T): T {
        val value = super.set(index, element)
        listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                listLiveDataState = ListLiveDataState.ITEM_RANGE_CHANGED,
                startIndex = index,
                itemCount = 1
        )
        return value
    }

    fun replace(elements: Collection<T>) {
        super.clear()
        super.addAll(elements)
        listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                listLiveDataState = ListLiveDataState.CHANGED
        )
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
        listStatusChangeNotificator.value = ListLiveDataChangeEvent(
                listLiveDataState = ListLiveDataState.ITEM_RANGE_REMOVED,
                startIndex = fromIndex,
                itemCount = toIndex - fromIndex
        )
    }
}