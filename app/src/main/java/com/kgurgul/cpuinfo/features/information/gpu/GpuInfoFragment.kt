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
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.FragmentRecyclerViewBinding
import com.kgurgul.cpuinfo.features.information.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Fragment which provides OpenGL info from custom [GLSurfaceView]
 *
 * @author kgurgul
 */
@AndroidEntryPoint
class GpuInfoFragment : BaseFragment<FragmentRecyclerViewBinding>(R.layout.fragment_recycler_view) {

    private val viewModel: GpuInfoViewModel by viewModels()

    private var glSurfaceView: GLSurfaceView? = null
    private val handler = Handler(Looper.getMainLooper())

    private val glRenderer = object : GLSurfaceView.Renderer {
        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
            viewModel.onGlInfoReceived(
                    gl.glGetString(GL10.GL_VENDOR),
                    gl.glGetString(GL10.GL_RENDERER),
                    gl.glGetString(GL10.GL_EXTENSIONS)
            )
            handler.post { glSurfaceView?.visibility = View.GONE }
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        }

        override fun onDrawFrame(gl: GL10) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        glSurfaceView = GLSurfaceView(requireActivity()).apply {
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            setRenderer(glRenderer)
        }
        binding.mainContainer.addView(glSurfaceView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = GpuInfoEpoxyController(requireContext())
        binding.recyclerView.adapter = controller.adapter
        viewModel.viewState.observe(viewLifecycleOwner, { controller.setData(it) })
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        glSurfaceView?.onPause()
        super.onPause()
    }
}