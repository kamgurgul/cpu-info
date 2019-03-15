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

package com.kgurgul.cpuinfo.utils

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import com.google.android.material.navigation.NavigationView
import java.lang.ref.WeakReference

/**
 * Modified version of NavigationUI class
 */
object NavigationUtils {

    private const val FRAGMENT_OPEN_DELAY = 300L

    /**
     * Setup [NavigationView] for passed [navController]
     */
    fun setupNavigationView(navigationView: NavigationView, navController: NavController) {
        navigationView.setNavigationItemSelectedListener(
                object : NavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        val handled = onNavDestinationSelected(item, navController)
                        if (handled) {
                            val parent = navigationView.parent
                            if (parent is DrawerLayout) {
                                parent.closeDrawer(navigationView)
                            }
                        }
                        return handled
                    }
                })
        val weakReference = WeakReference<NavigationView>(navigationView)
        navController.addOnDestinationChangedListener(
                object : NavController.OnDestinationChangedListener {
                    override fun onDestinationChanged(controller: NavController,
                                                      destination: NavDestination,
                                                      arguments: Bundle?) {
                        val view = weakReference.get()
                        if (view == null) {
                            navController.removeOnDestinationChangedListener(this)
                            return
                        }
                        val menu = view.menu
                        var h = 0
                        val size = menu.size()
                        while (h < size) {
                            val item = menu.getItem(h)
                            item.isChecked = matchDestination(destination, item.itemId)
                            h++
                        }
                    }
                })
    }

    private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
        val builder = NavOptions.Builder().setLaunchSingleTop(true)
        if (item.order and Menu.CATEGORY_SECONDARY == 0) {
            findStartDestination(navController.graph)?.let {
                builder.setPopUpTo(it.id, false)
            }
        }
        val options = builder.build()
        return try {
            Handler().postDelayed({ navController.navigate(item.itemId, null, options) },
                    FRAGMENT_OPEN_DELAY)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * Finds the actual start destination of the graph, handling cases where the graph's starting
     * destination is itself a NavGraph.
     */
    private fun findStartDestination(graph: NavGraph): NavDestination? {
        var startDestination: NavDestination? = graph
        while (startDestination is NavGraph) {
            startDestination = startDestination.findNode(startDestination.startDestination)
        }
        return startDestination
    }

    /**
     * Determines whether the given `destId` matches the NavDestination. This handles
     * both the default case (the destination's id matches the given id) and the nested case where
     * the given id is a parent/grandparent/etc of the destination.
     */
    private fun matchDestination(destination: NavDestination, destId: Int): Boolean {
        var currentDestination: NavDestination = destination
        while (currentDestination.id != destId && currentDestination.parent != null) {
            currentDestination = currentDestination.parent as NavDestination
        }
        return currentDestination.id == destId
    }
}