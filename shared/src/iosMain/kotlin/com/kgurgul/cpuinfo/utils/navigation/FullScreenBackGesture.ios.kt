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

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputEventHandler
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.requireLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.SceneInfo
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventHistory
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import kotlin.math.abs

// Thresholds of the Compose Multiplatform edge swipe, in points, so both gestures settle alike.
private const val COMPLETE_VELOCITY = 100f
private const val CANCEL_VELOCITY = 10f
private const val COMPLETE_FRACTION = 0.3f

/**
 * Drives the predictive back of the `NavDisplay` with a swipe towards the trailing edge started
 * anywhere in the content, alongside the edge swipe of Compose Multiplatform. It pops only pushed
 * screens, back handlers of the screens themselves (e.g. closing a search) stay on the edge swipe.
 */
@Composable
actual fun Modifier.fullScreenBackGesture(): Modifier {
    val dispatcher =
        LocalNavigationEventDispatcherOwner.current?.navigationEventDispatcher ?: return this
    return this then FullScreenBackGestureElement(dispatcher)
}

internal enum class BackDragIntent {
    Undecided,
    Back,
    Other,
}

/**
 * Decides what the drag of [totalChange] is once it crosses [touchSlop]. Children cross the same
 * slop first, in the main pass, so a child scroller (e.g. a pager) always wins the tie.
 */
internal fun backDragIntent(
    totalChange: Offset,
    touchSlop: Float,
    layoutDirection: LayoutDirection,
): BackDragIntent {
    val horizontal = abs(totalChange.x)
    return when {
        horizontal >= touchSlop ->
            if (totalChange.x * layoutDirection.backSign > 0f && horizontal > abs(totalChange.y)) {
                BackDragIntent.Back
            } else {
                BackDragIntent.Other
            }
        abs(totalChange.y) >= touchSlop -> BackDragIntent.Other
        else -> BackDragIntent.Undecided
    }
}

/** Whether the screen pops when the finger lifts, by the [velocity] and [translation] in pixels. */
internal fun shouldCompleteBack(
    velocity: Float,
    translation: Float,
    width: Float,
    density: Float,
): Boolean =
    when {
        velocity > COMPLETE_VELOCITY * density -> true
        velocity < -CANCEL_VELOCITY * density -> false
        else -> translation >= width * COMPLETE_FRACTION
    }

/** Feeds the swipe to the [NavigationEventDispatcher] like the system back gestures. */
internal class FullScreenBackGestureInput : NavigationEventInput() {
    private var isAdded = false
    private var history: NavigationEventHistory? = null
    private var isInProgress = false

    val canGoBack: Boolean
        get() = isAdded && history?.isScreenPop == true

    override fun onAdded(dispatcher: NavigationEventDispatcher) {
        isAdded = true
    }

    override fun onRemoved() {
        isAdded = false
        isInProgress = false
        history = null
    }

    override fun onHistoryChanged(history: NavigationEventHistory) {
        this.history = history
    }

    fun start(event: NavigationEvent) {
        if (!isAdded || isInProgress) return
        isInProgress = true
        dispatchOnBackStarted(event)
    }

    fun progress(event: NavigationEvent) {
        if (isInProgress) dispatchOnBackProgressed(event)
    }

    fun complete() {
        if (!isInProgress) return
        isInProgress = false
        dispatchOnBackCompleted()
    }

    fun cancel() {
        if (!isInProgress) return
        isInProgress = false
        dispatchOnBackCancelled()
    }
}

// Only the handler of NavDisplay reports a SceneInfo, the screens' own handlers report None.
private val NavigationEventHistory.isScreenPop: Boolean
    get() {
        val scene = (mergedHistory.getOrNull(currentIndex) as? SceneInfo<*>)?.scene
        return currentIndex > 0 && scene != null && scene !is OverlayScene<*>
    }

private val LayoutDirection.backSign: Float
    get() = if (this == LayoutDirection.Ltr) 1f else -1f

private data class FullScreenBackGestureElement(val dispatcher: NavigationEventDispatcher) :
    ModifierNodeElement<FullScreenBackGestureNode>() {
    override fun create() = FullScreenBackGestureNode(dispatcher)

    override fun update(node: FullScreenBackGestureNode) = node.update(dispatcher)
}

private class FullScreenBackGestureNode(private var dispatcher: NavigationEventDispatcher) :
    DelegatingNode() {

    private val input = FullScreenBackGestureInput()

    init {
        delegate(
            SuspendingPointerInputModifierNode(PointerInputEventHandler { detectBackGesture() })
        )
    }

    override fun onAttach() {
        dispatcher.addInput(input)
    }

    // A gesture left in progress would keep the dispatcher from starting any other back gesture.
    override fun onDetach() {
        input.cancel()
        dispatcher.removeInput(input)
    }

    fun update(dispatcher: NavigationEventDispatcher) {
        if (dispatcher == this.dispatcher) return
        if (isAttached) {
            input.cancel()
            this.dispatcher.removeInput(input)
            dispatcher.addInput(input)
        }
        this.dispatcher = dispatcher
    }

    private suspend fun PointerInputScope.detectBackGesture() {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (down.type == PointerType.Mouse || !input.canGoBack) return@awaitEachGesture
            val layoutDirection = requireLayoutDirection()
            val start = awaitBackDragStart(down.id, layoutDirection) ?: return@awaitEachGesture
            if (input.canGoBack) trackBackDrag(start, layoutDirection)
        }
    }

    private suspend fun AwaitPointerEventScope.awaitBackDragStart(
        pointerId: PointerId,
        layoutDirection: LayoutDirection,
    ): PointerInputChange? {
        var totalChange = Offset.Zero
        while (true) {
            val event = awaitPointerEvent()
            if (event.changes.count { it.pressed } > 1) return null
            val change = event.changes.firstOrNull { it.id == pointerId } ?: return null
            if (!change.pressed || change.isConsumed) return null
            totalChange += change.positionChange()
            when (backDragIntent(totalChange, viewConfiguration.touchSlop, layoutDirection)) {
                BackDragIntent.Undecided -> Unit
                BackDragIntent.Back -> return change
                BackDragIntent.Other -> return null
            }
        }
    }

    private suspend fun AwaitPointerEventScope.trackBackDrag(
        start: PointerInputChange,
        layoutDirection: LayoutDirection,
    ) {
        val backSign = layoutDirection.backSign
        val swipeEdge =
            if (layoutDirection == LayoutDirection.Ltr) {
                NavigationEvent.EDGE_LEFT
            } else {
                NavigationEvent.EDGE_RIGHT
            }
        val width = size.width.toFloat()
        val velocityTracker = VelocityTracker()

        fun PointerInputChange.toNavigationEvent() =
            NavigationEvent(
                touchX = position.x.coerceAtLeast(0f),
                touchY = position.y.coerceAtLeast(0f),
                progress = ((position.x - start.position.x) * backSign / width).coerceIn(0f, 1f),
                swipeEdge = swipeEdge,
                frameTimeMillis = uptimeMillis,
            )

        velocityTracker.addPointerInputChange(start)
        start.consume()
        input.start(start.toNavigationEvent())
        try {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == start.id } ?: return
                // A cancelled touch, e.g. taken by the system edge swipe, arrives as an already
                // consumed release.
                val isClaimedElsewhere = change.isConsumed
                event.changes.forEach { it.consume() }
                if (isClaimedElsewhere) return
                velocityTracker.addPointerInputChange(change)
                if (!change.pressed) {
                    val velocity = velocityTracker.calculateVelocity().x * backSign
                    val translation = (change.position.x - start.position.x) * backSign
                    if (shouldCompleteBack(velocity, translation, width, density)) {
                        input.complete()
                    }
                    return
                }
                input.progress(change.toNavigationEvent())
            }
        } finally {
            input.cancel()
        }
    }
}
