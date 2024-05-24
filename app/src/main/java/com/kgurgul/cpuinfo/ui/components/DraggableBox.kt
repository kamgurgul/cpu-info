package com.kgurgul.cpuinfo.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun DraggableBox(
    isRevealed: Boolean,
    state: DraggableBoxState,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    actionRow: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    key: Any = Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var actionRowOffset by remember { mutableIntStateOf(0) }
    val transitionState = remember { MutableTransitionState(false) }
    SideEffect {
        state.setRevealed(isRevealed)
        transitionState.targetState = isRevealed
    }
    val transition = rememberTransition(transitionState, "boxTransition")
    val offsetTransition by transition.animateFloat(
        label = "boxOffsetTransition",
        targetValueByState = { targetIsRevealed ->
            if (targetIsRevealed) offsetX - actionRowOffset else -offsetX
        },
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
                .pointerInput(key) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        val original = Offset(offsetX, 0f)
                        val summed = original + Offset(x = dragAmount, y = 0f)
                        val newValue = Offset(
                            x = summed.x.coerceIn(-actionRowOffset.toFloat(), 0f),
                            y = 0f
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

@Stable
class DraggableBoxState(
    isRevealed: Boolean = false,
) {
    internal var isRevealed by mutableStateOf(isRevealed)

    fun setRevealed(isRevealed: Boolean) {
        this.isRevealed = isRevealed
    }
}

@Composable
fun rememberDraggableBoxState(
    isRevealed: Boolean = false,
): DraggableBoxState {
    return remember { DraggableBoxState(isRevealed) }
}
