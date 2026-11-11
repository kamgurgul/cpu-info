/*
 * Copyright KG Soft
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
package com.kgurgul.cpuinfo.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.kgurgul.cpuinfo.utils.navigation.NavigationState
import com.kgurgul.cpuinfo.utils.navigation.Navigator
import com.kgurgul.cpuinfo.utils.navigation.rememberNavigationState
import com.kgurgul.cpuinfo.utils.navigation.toEntries
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalTestApi::class)
class NavigationStateTest {

    @Serializable
    private data object Home : NavKey

    @Serializable
    private data object Tab : NavKey

    @Serializable
    private data object TabDetails : NavKey

    @Serializable
    private data object Unknown : NavKey

    private val configuration = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(Home::class, Home.serializer())
                subclass(Tab::class, Tab.serializer())
                subclass(TabDetails::class, TabDetails.serializer())
                subclass(Unknown::class, Unknown.serializer())
            }
        }
    }

    private lateinit var navigationState: NavigationState
    private val navigator by lazy { Navigator(navigationState) }

    @Composable
    private fun TestHost(initialRoute: NavKey? = null) {
        navigationState =
            rememberNavigationState(
                startRoute = Home,
                topLevelRoutes = setOf(Home, Tab),
                configuration = configuration,
                parentRoutes = mapOf(TabDetails to Tab),
                initialRoute = initialRoute,
            )
        NavDisplay(
            entries =
                navigationState.toEntries(
                    entryProvider {
                        entry<Home> { Text("home") }
                        entry<Tab> { Text("tab") }
                        entry<TabDetails> { Text("details") }
                    }
                ),
            onBack = { navigator.goBack() },
        )
    }

    @Test
    fun startRouteIsDisplayed() = runComposeUiTest {
        setContent { TestHost() }

        onNodeWithText("home").assertIsDisplayed()
        assertEquals(Home, navigationState.currentRoute)
    }

    @Test
    fun backFromTopLevelRouteReturnsToStartRoute() = runComposeUiTest {
        setContent { TestHost() }

        runOnIdle { navigator.navigate(Tab) }
        onNodeWithText("tab").assertIsDisplayed()

        runOnIdle { navigator.goBack() }
        onNodeWithText("home").assertIsDisplayed()
        assertEquals(Home, navigationState.topLevelRoute)
    }

    @Test
    fun backStackOfTopLevelRouteIsRetained() = runComposeUiTest {
        setContent { TestHost() }

        runOnIdle {
            navigator.navigate(Tab)
            navigator.navigate(TabDetails)
        }
        onNodeWithText("details").assertIsDisplayed()

        runOnIdle { navigator.navigate(Home) }
        onNodeWithText("home").assertIsDisplayed()

        runOnIdle { navigator.navigate(Tab) }
        onNodeWithText("details").assertIsDisplayed()

        runOnIdle { navigator.goBack() }
        onNodeWithText("tab").assertIsDisplayed()
    }

    @Test
    fun reselectingTopLevelRoutePopsToRootOfItsStack() = runComposeUiTest {
        setContent { TestHost() }

        runOnIdle {
            navigator.navigate(Tab)
            navigator.navigate(TabDetails)
        }
        onNodeWithText("details").assertIsDisplayed()

        runOnIdle { navigator.navigate(Tab) }
        onNodeWithText("tab").assertIsDisplayed()
        assertEquals(listOf<NavKey>(Tab), navigationState.backStacks.getValue(Tab).toList())
    }

    @Test
    fun duplicateNavigationToSameRouteIsIgnored() = runComposeUiTest {
        setContent { TestHost() }

        runOnIdle {
            navigator.navigate(Tab)
            navigator.navigate(TabDetails)
            navigator.navigate(TabDetails)
        }
        onNodeWithText("details").assertIsDisplayed()
        assertEquals(
            listOf<NavKey>(Tab, TabDetails),
            navigationState.backStacks.getValue(Tab).toList(),
        )
    }

    @Test
    fun initialRouteIsOpenedWithItsParents() = runComposeUiTest {
        setContent { TestHost(initialRoute = TabDetails) }

        onNodeWithText("details").assertIsDisplayed()
        assertEquals(Tab, navigationState.topLevelRoute)

        runOnIdle { navigator.goBack() }
        onNodeWithText("tab").assertIsDisplayed()

        runOnIdle { navigator.goBack() }
        onNodeWithText("home").assertIsDisplayed()
    }

    @Test
    fun unknownInitialRouteIsIgnored() = runComposeUiTest {
        setContent { TestHost(initialRoute = Unknown) }

        onNodeWithText("home").assertIsDisplayed()
    }

    @Test
    fun restoreRebuildsBackStack() = runComposeUiTest {
        setContent { TestHost() }

        runOnIdle { assertTrue(navigator.restore(TabDetails)) }
        onNodeWithText("details").assertIsDisplayed()

        runOnIdle { assertTrue(navigator.restore(Tab)) }
        onNodeWithText("tab").assertIsDisplayed()
        assertEquals(listOf<NavKey>(Tab), navigationState.backStacks.getValue(Tab).toList())

        runOnIdle { assertTrue(navigator.restore(Home)) }
        onNodeWithText("home").assertIsDisplayed()

        runOnIdle { assertFalse(navigator.restore(Unknown)) }
        onNodeWithText("home").assertIsDisplayed()
    }
}
