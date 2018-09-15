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

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.di.ViewModelInjectionFactory
import com.kgurgul.cpuinfo.features.information.base.BaseRvFragment
import com.kgurgul.cpuinfo.features.information.base.InfoItemsAdapter
import com.kgurgul.cpuinfo.utils.DividerItemDecoration
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveDataObserver
import com.kgurgul.cpuinfo.utils.viewModelProvider
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
    private lateinit var infoItemsAdapter: InfoItemsAdapter

    private var glSurfaceView: GLSurfaceView? = null
    private val handler = Handler()

    private val glRenderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            val gpuInfoMap = HashMap<GpuInfoViewModel.GlInfoType, String?>()
            gpuInfoMap[GpuInfoViewModel.GlInfoType.GL_VENDOR] = gl.glGetString(GL10.GL_VENDOR)
            gpuInfoMap[GpuInfoViewModel.GlInfoType.GL_VERSION] = gl.glGetString(GL10.GL_VERSION)
            gpuInfoMap[GpuInfoViewModel.GlInfoType.GL_RENDERER] = gl.glGetString(GL10.GL_RENDERER)
            gpuInfoMap[GpuInfoViewModel.GlInfoType.GL_EXTENSIONS] = gl.glGetString(GL10.GL_EXTENSIONS)
            handler.post {
                glSurfaceView?.visibility = View.GONE
                viewModel.addGlInfo(gpuInfoMap)
            }
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        }

        override fun onDrawFrame(gl: GL10) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModelProvider(viewModelInjectionFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        if (!viewModel.isGlInfoStored()) {
            glSurfaceView = GLSurfaceView(activity)
            glSurfaceView?.apply {
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                setRenderer(glRenderer)
            }
            val mainContainer: ViewGroup = view.findViewById(R.id.main_container)
            mainContainer.addView(glSurfaceView)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    override fun setupRecyclerViewAdapter() {
        infoItemsAdapter = InfoItemsAdapter(viewModel.listLiveData,
                InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT, onClickListener = this)
        viewModel.listLiveData.listStatusChangeNotificator.observe(viewLifecycleOwner,
                ListLiveDataObserver(infoItemsAdapter))
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext()))
        recyclerView.adapter = infoItemsAdapter
    }
}