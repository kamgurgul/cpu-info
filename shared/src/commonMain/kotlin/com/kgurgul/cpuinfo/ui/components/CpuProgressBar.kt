package com.kgurgul.cpuinfo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CpuProgressBar(
    label: String,
    progress: Float,
    modifier: Modifier = Modifier,
    minMaxValues: Pair<String, String>? = null,
    prefixImageRes: DrawableResource? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    progressHeight: Dp = 16.dp,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
        )
        Spacer(modifier = Modifier.requiredSize(spacingSmall))
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(spacingXSmall)
        ) {
            var currentProgress by remember { mutableFloatStateOf(0f) }
            val progressAnimation by animateFloatAsState(
                targetValue = currentProgress,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                label = "__progressAnimation"
            )
            Row {
                prefixImageRes?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier
                            .requiredSize(progressHeight)
                            .background(
                                color = MaterialTheme.colorScheme.inverseSurface,
                                shape = MaterialTheme.shapes.small.copy(
                                    topEnd = ZeroCornerSize,
                                    bottomEnd = ZeroCornerSize,
                                ),
                            )
                            .padding(spacingSmall),
                    )
                }
                val strokeCap = if (prefixImageRes != null) {
                    StrokeCap.Butt
                } else {
                    StrokeCap.Round
                }
                LinearProgressIndicator(
                    progress = { progressAnimation },
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = Color.Unspecified,
                    strokeCap = strokeCap,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(progressHeight),
                )
                LaunchedEffect(progress) {
                    currentProgress = if (progress.isNaN()) 0.0f else progress
                }
            }

        }
        minMaxValues?.let {
            Spacer(modifier = Modifier.requiredSize(spacingXSmall))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = it.first,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                )
                Text(
                    text = it.second,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                )
            }
        }
    }
}
