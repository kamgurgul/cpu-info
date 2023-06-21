package com.kgurgul.cpuinfo.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableBox(
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    actionRow: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val offsetX = remember { mutableStateOf(0f) }
    var calculatedOffset by remember { mutableStateOf(0) }
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "boxTransition")
    val offsetTransition by transition.animateFloat(
        label = "boxOffsetTransition",
        transitionSpec = { tween(durationMillis = 500) },
        targetValueByState = { if (isRevealed) calculatedOffset - offsetX.value else -offsetX.value },
    )
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates -> calculatedOffset = coordinates.size.width },
        ) {
            actionRow()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset((offsetX.value + offsetTransition).roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        val original = Offset(offsetX.value, 0f)
                        val summed = original + Offset(x = dragAmount, y = 0f)
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, calculatedOffset.toFloat()),
                            y = 0f
                        )
                        if (newValue.x >= 10) {
                            onExpand()
                            return@detectHorizontalDragGestures
                        } else if (newValue.x <= 0) {
                            onCollapse()
                            return@detectHorizontalDragGestures
                        }
                        if (change.positionChange() != Offset.Zero) change.consume()
                        offsetX.value = newValue.x
                    }
                },
        ) {
            content()
        }
    }
}
