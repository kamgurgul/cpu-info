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
package com.kgurgul.cpuinfo.wear.navigation

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.util.lerp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.wear.compose.foundation.LocalReduceMotion
import androidx.wear.compose.foundation.SwipeToDismissBoxState
import androidx.wear.compose.foundation.SwipeToDismissKeys
import androidx.wear.compose.foundation.edgeSwipeToDismiss
import androidx.wear.compose.material3.SwipeToDismissBox
import androidx.wear.compose.navigation3.SwipeDismissableSceneStrategy

/**
 * Creates and remembers a [HoistedSwipeDismissableSceneStrategy].
 *
 * @param swipeToDismissBoxState state which can be used with [edgeSwipeToDismiss] by the entries
 *   which handle horizontal gestures on their own
 */
@Composable
fun <T : Any> rememberHoistedSwipeDismissableSceneStrategy(
    swipeToDismissBoxState: SwipeToDismissBoxState
): SceneStrategy<T> =
    remember(swipeToDismissBoxState) {
        HoistedSwipeDismissableSceneStrategy(swipeToDismissBoxState)
    }

/**
 * [SwipeDismissableSceneStrategy] which uses hoisted [SwipeToDismissBoxState].
 *
 * The strategy from the Wear Compose library creates the state of its swipe to dismiss box
 * internally, so it can't be combined with [edgeSwipeToDismiss]. API level 36 onwards the platform
 * predictive back is used instead of the swipe to dismiss box, so it is delegated to the library.
 */
class HoistedSwipeDismissableSceneStrategy<T : Any>(
    private val swipeToDismissBoxState: SwipeToDismissBoxState
) : SceneStrategy<T> {

    private val predictiveBackSceneStrategy = SwipeDismissableSceneStrategy<T>()

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (entries.isEmpty()) return null
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return with(predictiveBackSceneStrategy) { calculateScene(entries) }
        }
        val previousEntries = entries.dropLast(1)
        return HoistedSwipeToDismissScene(
            swipeToDismissBoxState = swipeToDismissBoxState,
            onBack = onBack,
            currentEntry = entries.last(),
            background = previousEntries.lastOrNull(),
            previousEntries = previousEntries,
            currentBackStack = entries,
        )
    }
}

/**
 * Based on the `SwipeToDismissScene` from `androidx.wear.compose:compose-navigation3` (Apache 2.0).
 * The only difference is the hoisted [swipeToDismissBoxState].
 */
private class HoistedSwipeToDismissScene<T : Any>(
    private val swipeToDismissBoxState: SwipeToDismissBoxState,
    onBack: () -> Unit,
    val currentEntry: NavEntry<T>,
    background: NavEntry<T>?,
    override val previousEntries: List<NavEntry<T>>,
    currentBackStack: List<NavEntry<T>>,
) : Scene<T> {
    // A Unit scene key disables animations in NavDisplay, so that this scene can internally
    // handle animations for navigation forward and back
    override val key: Any = Unit

    override val entries: List<NavEntry<T>> = listOf(currentEntry)

    // There could be a delay from when onDismissed is called to Nav3 actually updating the
    // current entry. Track it to render the background (now foreground) entry instead of the
    // entry that has already been swiped away.
    private var onDismissedCalled by mutableStateOf(false)

    override val content: @Composable (() -> Unit) = {
        val previousBackStack = rememberSaveable { mutableListOf<Any>() }
        val currentBackStackMapped = currentBackStack.map { it.contentKey }
        val isPop =
            previousBackStack.isNotEmpty() && isPop(previousBackStack, currentBackStackMapped)
        previousBackStack.clear()
        previousBackStack.addAll(currentBackStackMapped)

        val reduceMotionEnabled = LocalReduceMotion.current
        val animationProgress =
            remember(currentEntry) { if (!isPop) Animatable(0f) else Animatable(1f) }
        val isRoundDevice = LocalConfiguration.current.isScreenRound

        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            onDismissed = {
                onDismissedCalled = true
                onBack()
            },
            backgroundKey = background?.contentKey ?: SwipeToDismissKeys.Background,
            contentKey = currentEntry.contentKey,
            userSwipeEnabled = background != null,
        ) { isBackground ->
            BoxedStackEntryContent(
                entry = if (isBackground || onDismissedCalled) background else currentEntry,
                modifier =
                    if (isBackground) {
                        Modifier
                    } else {
                        Modifier.graphicsLayer {
                            val scaleProgression =
                                EnterTransitionEasing.transform(animationProgress.value / 0.75f)
                            val alphaProgression =
                                EnterTransitionEasing.transform(animationProgress.value / 0.25f)
                            val scale = lerp(0.75f, 1f, scaleProgression).coerceAtMost(1f)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(0.1f, 1f, alphaProgression).coerceIn(0f, 1f)
                            clip = true
                            shape = if (isRoundDevice) CircleShape else RectangleShape
                        }
                    },
                layerColor = if (isBackground || isPop) Color.Unspecified else Color.White,
                isRoundDevice = isRoundDevice,
                animatable = animationProgress,
            )
        }

        // Runs transitions but only for navigates, not pops
        LaunchedEffect(currentEntry) {
            if (!isPop) {
                if (reduceMotionEnabled) {
                    animationProgress.snapTo(1f)
                } else {
                    animationProgress.animateTo(
                        targetValue = 1f,
                        animationSpec =
                            tween(
                                durationMillis = ENTER_TRANSITION_DURATION,
                                easing = LinearEasing,
                            ),
                    )
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HoistedSwipeToDismissScene<*>
        return swipeToDismissBoxState == other.swipeToDismissBoxState &&
            currentEntry == other.currentEntry &&
            previousEntries == other.previousEntries
    }

    override fun hashCode(): Int {
        var result = swipeToDismissBoxState.hashCode()
        result = 31 * result + currentEntry.hashCode()
        result = 31 * result + previousEntries.hashCode()
        return result
    }
}

@Composable
private fun <T : Any> BoxedStackEntryContent(
    entry: NavEntry<T>?,
    modifier: Modifier,
    layerColor: Color,
    isRoundDevice: Boolean,
    animatable: Animatable<Float, AnimationVector1D>,
) {
    if (entry != null) {
        Box(modifier, propagateMinConstraints = true) {
            entry.Content()
            // Flash effect when a new screen opens
            if (layerColor != Color.Unspecified) {
                Canvas(Modifier.fillMaxSize()) {
                    val absoluteProgression = ((animatable.value - 0.25f) / 0.75f).coerceIn(0f, 1f)
                    val easedProgression = EnterTransitionEasing.transform(absoluteProgression)
                    val alpha = lerp(0.07f, 0f, easedProgression).coerceIn(0f, 1f)
                    if (isRoundDevice) {
                        drawCircle(color = layerColor.copy(alpha))
                    } else {
                        drawRect(color = layerColor.copy(alpha))
                    }
                }
            }
        }
    }
}

private fun <T : Any> isPop(oldBackStack: List<T>, newBackStack: List<T>): Boolean {
    // Entire stack replaced
    if (oldBackStack.first() != newBackStack.first()) return false
    // Navigated
    if (newBackStack.size > oldBackStack.size) return false
    val divergingIndex =
        newBackStack.indices.firstOrNull { index -> newBackStack[index] != oldBackStack[index] }
    // If the new stack never diverged from the old one then it is its clean subset and it is a pop
    return divergingIndex == null && newBackStack.size != oldBackStack.size
}

private const val ENTER_TRANSITION_DURATION = 400
private val EnterTransitionEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
