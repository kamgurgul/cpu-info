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

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList
import java.util.*

/**
 * Copy from [android.databinding.ObservableArrayList] which makes adapters happy with animations.
 * It adds [replace] method.
 *
 * @author kgurgul
 */
class AdapterArrayList<T> : ArrayList<T>(), ObservableList<T> {

    @Transient
    private var mListeners: ListChangeRegistry? = null

    override fun addOnListChangedCallback(
            listener: ObservableList.OnListChangedCallback<out ObservableList<T>>?) {
        if (mListeners == null) {
            mListeners = ListChangeRegistry()
        }
        mListeners!!.add(listener)
    }

    override fun removeOnListChangedCallback(
            listener: ObservableList.OnListChangedCallback<out ObservableList<T>>?) {
        mListeners?.remove(listener)
    }

    override fun add(element: T): Boolean {
        super.add(element)
        notifyAdd(size - 1, 1)
        return true
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        notifyAdd(index, 1)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val oldSize = size
        val added = super.addAll(elements)
        if (added) {
            notifyAdd(oldSize, size - oldSize)
        }
        return added
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val added = super.addAll(index, elements)
        if (added) {
            notifyAdd(index, elements.size)
        }
        return added
    }

    override fun clear() {
        val oldSize = size
        super.clear()
        if (oldSize != 0) {
            notifyRemove(0, oldSize)
        }
    }

    override fun removeAt(index: Int): T {
        val value = super.removeAt(index)
        notifyRemove(index, 1)
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
        mListeners?.notifyChanged(this, index, 1)
        return value
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
        notifyRemove(fromIndex, toIndex - fromIndex)
    }

    fun replace(elements: Collection<T>) {
        super.clear()
        super.addAll(elements)
        mListeners?.notifyChanged(this)
    }

    private fun notifyAdd(start: Int, count: Int) {
        mListeners?.notifyInserted(this, start, count)
    }

    private fun notifyRemove(start: Int, count: Int) {
        mListeners?.notifyRemoved(this, start, count)
    }
}