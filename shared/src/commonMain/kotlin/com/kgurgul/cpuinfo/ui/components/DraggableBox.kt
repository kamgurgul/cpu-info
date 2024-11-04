package com.kgurgul.cpuinfo.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DraggableBox(
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier,
    key: Any = Unit,
    actionRow: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var actionRowOffset by remember { mutableIntStateOf(0) }
    val transitionState = remember { MutableTransitionState(false) }
    SideEffect {
        transitionState.targetState = isRevealed
    }
    val transition = rememberTransition(transitionState, "boxTransition")
    val offsetTransition by transition.animateFloat(
        label = "boxOffsetTransition",
        targetValueByState = { targetIsRevealed ->
            if (targetIsRevealed) offsetX - actionRowOffset else -offsetX
        },
    )
    val elevationTransition by transition.animateDp(
        label = "boxElevationTransition",
        targetValueByState = { if (isRevealed) 40.dp else 0.dp }
    )
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates -> actionRowOffset = coordinates.size.width },
        ) {
            actionRow()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset((offsetX + offsetTransition).roundToInt(), 0) }
                .shadow(
                    elevation = elevationTransition,
                    spotColor = MaterialTheme.colorScheme.onBackground
                )
                .pointerInput(key) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        val original = Offset(offsetX, 0f)
                        val summed = original + Offset(x = dragAmount, y = 0f)
                        val newValue = Offset(
                            x = summed.x.coerceIn(-actionRowOffset.toFloat(), 0f),
                            y = 0f,
                        )
                        if (newValue.x <= -10) {
                            onExpand()
                            return@detectHorizontalDragGestures
                        } else if (newValue.x >= 0) {
                            onCollapse()
                            return@detectHorizontalDragGestures
                        }
                        if (change.positionChange() != Offset.Zero) change.consume()
                        offsetX = newValue.x
                    }
                },
        ) {
            content()
        }
    }
}
