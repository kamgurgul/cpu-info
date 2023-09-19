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

package com.kgurgul.cpuinfo.features

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.ActivityHostLayoutBinding
import com.kgurgul.cpuinfo.utils.setupEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

/**
 * Base activity which is a host for whole application.
 *
 * @author kgurgul
 */
@AndroidEntryPoint
class HostActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHostLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppThemeBase)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_layout)
        setupEdgeToEdge()
        setupNavigation()
        setSupportActionBar(binding.toolbar)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // Processes cannot be listed above M
            val menu = binding.bottomNavigation.menu
            menu.findItem(R.id.processes).isVisible = false
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    private fun setupNavigation() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            setToolbarTitleAndElevation(destination.label.toString())
        }
        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnItemReselectedListener {
                // Do nothing - TODO: scroll to top
            }
        }
    }

    /**
     * Set toolbar title and manage elevation in case of L+ devices and TabLayout
     */
    @SuppressLint("NewApi")
    private fun setToolbarTitleAndElevation(title: String) {
        binding.toolbar.title = title
        binding.toolbar.isVisible = navController.currentDestination?.id != R.id.applications
                && navController.currentDestination?.id != R.id.temperature
                && navController.currentDestination?.id != R.id.processes
        if (navController.currentDestination?.id == R.id.hardware) {
            binding.toolbar.elevation = 0f
        } else {
            binding.toolbar.elevation = resources.getDimension(R.dimen.elevation_height)
        }
    }
}