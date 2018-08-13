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
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.ActivityHostLayoutBinding
import com.kgurgul.cpuinfo.utils.isTablet
import com.kgurgul.cpuinfo.utils.runOnApiAbove
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * Base activity which is a host for whole application.
 *
 * @author kgurgul
 */
class HostActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHostLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_layout)
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnNavigatedListener { _, destination ->
            setToolbarTitleAndElevation(destination.label.toString())
        }
        setSupportActionBar(binding.toolbar)
        binding.navigationView.setupWithNavController(navController)
        runOnApiAbove(Build.VERSION_CODES.M) {
            // Processes cannot be listed above M
            val menu = binding.navigationView.menu
            menu.findItem(R.id.processes).isVisible = false
        }
        if (!isTablet()) {
            val actionBarDrawerToggle = getDrawerToggle()
            binding.drawerLayout?.addDrawerListener(actionBarDrawerToggle)
            actionBarDrawerToggle.syncState()
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun onBackPressed() {
        if (binding.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding.drawerLayout?.closeDrawers()
            return
        }
        super.onBackPressed()
    }

    /**
     * Set toolbar title and manage elevation in case of L+ devices and TabLayout
     */
    @SuppressLint("NewApi")
    private fun setToolbarTitleAndElevation(title: String) {
        binding.toolbar.title = title
        runOnApiAbove(Build.VERSION_CODES.KITKAT_WATCH) {
            if (navController.currentDestination?.id == R.id.hardware) {
                binding.toolbar.elevation = 0f
            } else {
                binding.toolbar.elevation = resources.getDimension(R.dimen.elevation_height)
            }
        }
    }

    /**
     * Simple drawer toggle without extra logic
     */
    private fun getDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.open_drawer, R.string.close_drawer)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
}