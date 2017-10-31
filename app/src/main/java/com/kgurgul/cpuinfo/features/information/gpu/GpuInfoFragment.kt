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

package com.kgurgul.cpuinfo.features.information.gpu

import android.arch.lifecycle.ViewModelProviders
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.DividerItemDecoration
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import com.kgurgul.cpuinfo.utils.Utils
import java.util.*
import javax.inject.Inject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Fragment which extends [BaseRvFragment] to provide OpenGL info from custom [GLSurfaceView]
 *
 * @author kgurgul
 */
class GpuInfoFragment : BaseRvFragment() {

    @Inject
    lateinit var viewModelInjectionFactory: ViewModelInjectionFactory<GpuInfoViewModel>
    private lateinit var viewModel: GpuInfoViewModel

    private val infoItemsAdapter: InfoItemsAdapter by lazy {
        InfoItemsAdapter(context, viewModel.dataObservableList,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT)
    }

    private val gpuInfoList = ArrayList<Pair<String, String>>()
    private var glSurfaceView: GLSurfaceView? = null
    private lateinit var handler: Handler
    private val glRenderer = object : GLSurfaceView.Renderer {

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            Utils.addPairIfExists(gpuInfoList, getString(R.string.vendor), gl.glGetString(GL10.GL_VENDOR))
            Utils.addPairIfExists(gpuInfoList, getString(R.string.version), gl.glGetString(GL10.GL_VERSION))
            Utils.addPairIfExists(gpuInfoList, getString(R.string.renderer), gl.glGetString(GL10.GL_RENDERER))
            Utils.addPairIfExists(gpuInfoList, getString(R.string.extensions), gl.glGetString(GL10.GL_EXTENSIONS))

            handler.sendEmptyMessage(0)
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        }

        override fun onDrawFrame(gl: GL10) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelInjectionFactory)
                .get(GpuInfoViewModel::class.java)
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                glSurfaceView?.visibility = View.GONE
                viewModel.addGlInfo(gpuInfoList)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        if (!viewModel.isGlInfoStored()) {
            glSurfaceView = GLSurfaceView(activity)
            glSurfaceView?.setRenderer(glRenderer)

            val mainContainer: ViewGroup = view.findViewById(R.id.main_container)
            mainContainer.addView(glSurfaceView)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        infoItemsAdapter.registerListChangeNotifier()
    }

    override fun onStop() {
        infoItemsAdapter.unregisterListChangeNotifier()
        super.onStop()
    }

    override fun setupRecyclerViewAdapter() {
        recyclerView.addItemDecoration(DividerItemDecoration(context))
        recyclerView.adapter = infoItemsAdapter
    }
}