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
package com.kgurgul.cpuinfo.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneInfo
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FullScreenBackGestureTest {

    private val dispatcher = NavigationEventDispatcher()
    private val input = FullScreenBackGestureInput().also { dispatcher.addInput(it) }

    @Test
    fun cannotGoBackWithoutBackHandler() {
        assertFalse(input.canGoBack)
    }

    @Test
    fun cannotGoBackFromRootScreen() {
        dispatcher.addHandler(TestHandler(SceneInfo(TestScene("root")), isBackEnabled = false))

        assertFalse(input.canGoBack)
    }

    @Test
    fun popsPushedScreen() {
        val handler = pushedScreenHandler()
        dispatcher.addHandler(handler)

        assertTrue(input.canGoBack)
        input.start(event(progress = 0f))
        input.progress(event(progress = 0.5f))
        input.complete()

        assertEquals(1, handler.completions)
        assertEquals(0, handler.cancellations)
    }

    @Test
    fun staysOnScreenWhenSwipeIsCancelled() {
        val handler = pushedScreenHandler()
        dispatcher.addHandler(handler)

        input.start(event(progress = 0f))
        input.progress(event(progress = 0.2f))
        input.cancel()
        input.complete()

        assertEquals(0, handler.completions)
        assertEquals(1, handler.cancellations)
    }

    @Test
    fun leavesBackHandlersOfScreenToEdgeSwipe() {
        dispatcher.addHandler(pushedScreenHandler())
        dispatcher.addHandler(TestHandler(NavigationEventInfo.None))

        assertFalse(input.canGoBack)
    }

    @Test
    fun doesNotDismissOverlay() {
        dispatcher.addHandler(
            TestHandler(
                info = SceneInfo(TestOverlayScene("dialog")),
                backInfo = listOf(SceneInfo(TestScene("list"))),
            )
        )

        assertFalse(input.canGoBack)
    }

    @Test
    fun cannotGoBackOnceRemovedFromDispatcher() {
        dispatcher.addHandler(pushedScreenHandler())

        dispatcher.removeInput(input)

        assertFalse(input.canGoBack)
    }

    @Test
    fun startsBackDragOnHorizontalSwipeTowardsTrailingEdge() {
        assertEquals(
            BackDragIntent.Back,
            backDragIntent(Offset(10f, 4f), touchSlop = 10f, LayoutDirection.Ltr),
        )
        assertEquals(
            BackDragIntent.Back,
            backDragIntent(Offset(-10f, 4f), touchSlop = 10f, LayoutDirection.Rtl),
        )
    }

    @Test
    fun waitsUntilSwipeCrossesTouchSlop() {
        assertEquals(
            BackDragIntent.Undecided,
            backDragIntent(Offset(9f, 9f), touchSlop = 10f, LayoutDirection.Ltr),
        )
    }

    @Test
    fun givesUpOnSwipesThatAreNotBackSwipe() {
        assertEquals(
            BackDragIntent.Other,
            backDragIntent(Offset(-10f, 0f), touchSlop = 10f, LayoutDirection.Ltr),
        )
        assertEquals(
            BackDragIntent.Other,
            backDragIntent(Offset(10f, 0f), touchSlop = 10f, LayoutDirection.Rtl),
        )
        assertEquals(
            BackDragIntent.Other,
            backDragIntent(Offset(4f, 10f), touchSlop = 10f, LayoutDirection.Ltr),
        )
        assertEquals(
            BackDragIntent.Other,
            backDragIntent(Offset(10f, 12f), touchSlop = 10f, LayoutDirection.Ltr),
        )
    }

    @Test
    fun completesOnFlickEvenWhenScreenBarelyMoved() {
        assertTrue(shouldCompleteBack(velocity = 300f, translation = 20f, width = 390f, 2f))
    }

    @Test
    fun cancelsWhenFingerMovesBackBeforeLifting() {
        assertFalse(shouldCompleteBack(velocity = -30f, translation = 300f, width = 390f, 2f))
    }

    @Test
    fun settlesByDistanceWhenFingerLiftsSlowly() {
        assertTrue(shouldCompleteBack(velocity = 0f, translation = 120f, width = 390f, 2f))
        assertFalse(shouldCompleteBack(velocity = 0f, translation = 110f, width = 390f, 2f))
    }

    private fun pushedScreenHandler() =
        TestHandler(
            info = SceneInfo(TestScene("details")),
            backInfo = listOf(SceneInfo(TestScene("list"))),
        )

    private fun event(progress: Float) =
        NavigationEvent(
            touchX = 0f,
            touchY = 0f,
            progress = progress,
            swipeEdge = NavigationEvent.EDGE_LEFT,
        )
}

private class TestHandler(
    info: NavigationEventInfo,
    backInfo: List<NavigationEventInfo> = emptyList(),
    isBackEnabled: Boolean = true,
) : NavigationEventHandler<NavigationEventInfo>(info, isBackEnabled) {
    var completions = 0
    var cancellations = 0

    init {
        setInfo(currentInfo = info, backInfo = backInfo)
    }

    override fun onBackCompleted() {
        completions++
    }

    override fun onBackCancelled() {
        cancellations++
    }
}

private class TestScene(override val key: Any) : Scene<String> {
    override val entries: List<NavEntry<String>> = emptyList()
    override val previousEntries: List<NavEntry<String>> = emptyList()
    override val content: @Composable () -> Unit = {}
}

private class TestOverlayScene(override val key: Any) : OverlayScene<String> {
    override val entries: List<NavEntry<String>> = emptyList()
    override val previousEntries: List<NavEntry<String>> = emptyList()
    override val overlaidEntries: List<NavEntry<String>> = emptyList()
    override val content: @Composable () -> Unit = {}
}
