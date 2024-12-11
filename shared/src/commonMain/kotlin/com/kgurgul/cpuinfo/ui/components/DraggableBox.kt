package com.kgurgul.cpuinfo.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.overscroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest

private const val VELOCITY_THRESHOLD = 125
private const val SNAP_ANIMATION_MS = 150

@Composable
fun DraggableBox(
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    actionRowOffset: Float,
    actionRow: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val state = remember {
        AnchoredDraggableState(
            initialValue = if (isRevealed) DraggableState.Revealed else DraggableState.Collapsed,
            positionalThreshold = { velocity: Float -> velocity * 0.5f },
            velocityThreshold = { with(density) { VELOCITY_THRESHOLD.dp.toPx() } },
            snapAnimationSpec = tween(durationMillis = SNAP_ANIMATION_MS),
            decayAnimationSpec = decayAnimationSpec,
        )
    }
    val anchors = DraggableAnchors {
        DraggableState.Collapsed at 0f
        DraggableState.Revealed at -actionRowOffset
    }
    SideEffect {
        state.updateAnchors(anchors)
    }
    LaunchedEffect(isRevealed) {
        val target = if (isRevealed) DraggableState.Revealed else DraggableState.Collapsed
        if (target != state.settledValue) {
            state.animateTo(target)
        }
    }
    val overscrollEffect = ScrollableDefaults.overscrollEffect()
    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }
            .collectLatest {
                if (it == DraggableState.Revealed) {
                    onExpand()
                } else {
                    onCollapse()
                }
            }
    }
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        actionRow()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                    overscrollEffect = overscrollEffect,
                )
                .overscroll(overscrollEffect)
                .offset {
                    IntOffset(x = state.requireOffset().roundToInt(), y = 0)
                }
                .shadow(
                    elevation = calculateElevation(state.offset, actionRowOffset),
                    spotColor = MaterialTheme.colorScheme.onBackground,
                ),
        ) {
            content()
        }
    }
}

private fun calculateElevation(
    offset: Float,
    actionRowOffset: Float,
    maxElevation: Dp = 8.dp
): Dp {
    return if (offset.absoluteValue == 0f) {
        0.dp
    } else {
        (offset.absoluteValue * 100f / actionRowOffset.absoluteValue) / 100f * maxElevation
    }
}

enum class DraggableState {
    Collapsed,
    Revealed
}
