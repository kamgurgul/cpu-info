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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.kgurgul.cpuinfo.R
import dagger.hilt.android.AndroidEntryPoint


/**
 * Simple base for all info fragments which displays data on [RecyclerView]
 *
 * @author kgurgul
 */
@AndroidEntryPoint
abstract class BaseRvFragment : Fragment(), InfoItemsAdapter.OnClickListener {

    protected lateinit var mainContainer: View
    protected lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_rv, container, false)
        mainContainer = view.findViewById(R.id.main_container)
        recyclerView = view.findViewById(R.id.recycler_view)
        setupRecyclerView()
        setupRecyclerViewAdapter()
        return view
    }

    override fun onDestroyView() {
        recyclerView.adapter = null
        super.onDestroyView()
    }

    /**
     * Basic [RecyclerView] configuration with [LinearLayoutManager] and disabled animations
     */
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        // Remove change animation
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    /**
     * Setup adapter for [RecyclerView]. Use this method to set adapter and refreshing logic.
     */
    abstract fun setupRecyclerViewAdapter()

    override fun onItemLongPressed(item: Pair<String, String>) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager
        val clip = ClipData.newPlainText(requireContext().getString(R.string.app_name),
                item.second)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(mainContainer, R.string.text_copied, Snackbar.LENGTH_SHORT).show()
    }
}