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

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.unveilIn
import androidx.compose.animation.veilOut
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.navigationevent.NavigationEvent.Companion.EDGE_LEFT

/**
 * Spring "response" of the push/pop animation, the iOS notion of how long the motion takes.
 *
 * Compose asks for a stiffness instead, which relates to the response as `stiffness = (2 * PI /
 * response) ^ 2`, so `800` is a response of roughly `0.22s`. A critically damped spring spends a
 * long time creeping through the last few percent, so it has to be stiffer than the response alone
 * suggests to feel as quick as it reads: this one is 90% done after `140ms` and 99% done after
 * `235ms`, against `195ms` and `345ms` for the curve `NavDisplay` ships.
 *
 * Raising this much further stops buying speed, a full width slide landing in under `120ms` reads
 * as a cut rather than as movement.
 */
private const val IOS_SPRING_STIFFNESS = 800f

/**
 * Critically damped so the screens never overshoot. Any bounce would drag the incoming screen past
 * the edge of the window and expose the background behind it.
 */
private const val IOS_SPRING_DAMPING = Spring.DampingRatioNoBouncy

/** How far the screen underneath trails behind the one on top, the iOS parallax. */
private const val PARALLAX_FRACTION = 4

/**
 * Duration of the interactive back gesture. Only the settle after the finger lifts actually plays,
 * the rest is driven by the gesture itself, so this is short enough to match how quickly the
 * [iosSlideSpring] lands.
 */
private const val PREDICTIVE_POP_DURATION_MILLISECOND = 250

/** Slide spec of both screens, shared so they stay locked together for the whole animation. */
private val iosSlideSpring: FiniteAnimationSpec<IntOffset> =
    spring(
        dampingRatio = IOS_SPRING_DAMPING,
        stiffness = IOS_SPRING_STIFFNESS,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    )

/** Dimming spec of the screen underneath, kept on the same spring as [iosSlideSpring]. */
private val iosVeilSpring: FiniteAnimationSpec<Color> =
    spring(
        dampingRatio = IOS_SPRING_DAMPING,
        stiffness = IOS_SPRING_STIFFNESS,
    )

/**
 * The iOS flavoured slide animations `NavDisplay` uses when no transition is passed to it.
 *
 * These mirror the `NavDisplay` defaults but swap their fixed duration curve for a spring, so the
 * motion is driven by velocity the way iOS 26 transitions are: it starts fast, decelerates into
 * place, and picks up from wherever it currently is when a navigation interrupts another one
 * instead of restarting from the beginning.
 *
 * The predictive pop stays on a linear curve on purpose, its progress is the back gesture itself
 * and has to track the finger one to one.
 */
@OptIn(ExperimentalAnimationApi::class)
private val defaultNavTransitionSpecs =
    NavTransitionSpecs(
        transitionSpec = {
            ContentTransform(
                slideIntoContainer(
                    towards = SlideDirection.Left,
                    animationSpec = iosSlideSpring,
                ),
                slideOutOfContainer(
                    towards = SlideDirection.Left,
                    targetOffset = { it / PARALLAX_FRACTION },
                    animationSpec = iosSlideSpring,
                ) + veilOut(animationSpec = iosVeilSpring),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                slideIntoContainer(
                    towards = SlideDirection.Right,
                    initialOffset = { it / PARALLAX_FRACTION },
                    animationSpec = iosSlideSpring,
                ) + unveilIn(animationSpec = iosVeilSpring),
                slideOutOfContainer(
                    towards = SlideDirection.Right,
                    animationSpec = iosSlideSpring,
                ),
            )
        },
        predictivePopTransitionSpec = { edge ->
            val towards = if (edge == EDGE_LEFT) SlideDirection.Right else SlideDirection.Left
            val gestureSpec =
                tween<IntOffset>(
                    durationMillis = PREDICTIVE_POP_DURATION_MILLISECOND,
                    easing = LinearEasing,
                )
            val gestureVeilSpec =
                tween<Color>(
                    durationMillis = PREDICTIVE_POP_DURATION_MILLISECOND,
                    easing = LinearEasing,
                )
            ContentTransform(
                slideIntoContainer(
                    towards = towards,
                    initialOffset = { it / PARALLAX_FRACTION },
                    animationSpec = gestureSpec,
                ) + unveilIn(animationSpec = gestureVeilSpec),
                slideOutOfContainer(
                    towards = towards,
                    animationSpec = gestureSpec,
                ),
            )
        },
    )

actual fun navTransitionSpecs(isBottomBarNavigation: Boolean): NavTransitionSpecs =
    if (isBottomBarNavigation) fadeNavTransitionSpecs else defaultNavTransitionSpecs

internal actual val exitThroughStartRoute: Boolean = false
