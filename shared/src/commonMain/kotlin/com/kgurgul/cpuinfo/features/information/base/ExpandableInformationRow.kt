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
package com.kgurgul.cpuinfo.features.information.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.collapse
import com.kgurgul.cpuinfo.shared.expand
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExpandableInformationRow(
    title: String,
    value: String,
    isExpanded: Boolean,
    isLastItem: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor =
        if (value.isEmpty()) {
            MaterialTheme.colorScheme.tertiaryFixed
        } else {
            MaterialTheme.colorScheme.onBackground
        }
    val arrowRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                        onClickLabel =
                            stringResource(
                                if (isExpanded) Res.string.collapse else Res.string.expand
                            ),
                        onClick = onClick,
                    ),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                modifier = Modifier.weight(if (value.isEmpty()) 1f else .4f),
            )
            if (value.isNotEmpty()) {
                Spacer(modifier = Modifier.size(spacingXSmall))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = .7f),
                    modifier = Modifier.weight(.6f),
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiaryFixed,
                modifier = Modifier.rotate(arrowRotation),
            )
        }
        if (!isLastItem) {
            CpuDivider(modifier = Modifier.padding(top = spacingSmall))
        }
    }
}

@Preview
@Composable
fun ExpandableInformationRowPreview() {
    CpuInfoTheme {
        ExpandableInformationRow(
            title = "Title",
            value = "Value",
            isExpanded = false,
            isLastItem = true,
            onClick = {},
        )
    }
}
