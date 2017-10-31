package com.kgurgul.cpuinfo.common.list

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * To avoid problems with Android LifecycleOwner and LifecycleObserver
 * (https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808)
 * we have to notify adapter about onStart and onStop manually.
 *
 * @author kgurgul
 */
@Suppress("LeakingThis")
abstract class ObservableListAdapter<T, VH : RecyclerView.ViewHolder>(
        private val observableList: ObservableList<T>
) : RecyclerView.Adapter<VH>() {

    private val simpleListChangeNotifier =
            SimpleListChangeNotifier<T>(WeakReference(this))

    fun registerListChangeNotifier() {
        observableList.addOnListChangedCallback(simpleListChangeNotifier)
    }

    fun unregisterListChangeNotifier() {
        observableList.removeOnListChangedCallback(simpleListChangeNotifier)
    }
}