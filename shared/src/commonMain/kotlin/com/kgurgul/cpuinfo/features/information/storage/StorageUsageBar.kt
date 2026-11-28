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
package com.kgurgul.cpuinfo.features.information.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.baseline_sd_storage_24
import com.kgurgul.cpuinfo.shared.storage_usage_summary
import com.kgurgul.cpuinfo.shared.storage_usage_unavailable
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun StorageUsageBar(
    storageItem: StorageItem,
    progressHeight: Dp,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleSmall,
    progressColor: Color = MaterialTheme.colorScheme.tertiaryFixed,
) {
    val title = storageItem.label.asString().trim()
    val available = storageItem.storageAvailable
    val summary =
        if (available == null) {
            stringResource(Res.string.storage_usage_unavailable)
        } else {
            stringResource(
                Res.string.storage_usage_summary,
                Utils.humanReadableByteCount(storageItem.storageUsed),
                Utils.humanReadableByteCount(storageItem.storageTotal),
                formatUsedPercent(storageItem.storageUsed, storageItem.storageTotal),
                Utils.humanReadableByteCount(available),
            )
        }
    val progress =
        if (available == null) 0f
        else (storageItem.storageUsed.toDouble() / storageItem.storageTotal).toFloat()
    CpuProgressBar(
        progress = progress,
        modifier = modifier,
        label = if (title.isEmpty()) summary else "$title\n$summary",
        prefixImageRes = storageItem.iconDrawable,
        textColor = textColor,
        progressColor = progressColor,
        progressHeight = progressHeight,
        titleTextStyle = titleTextStyle,
    )
}

/**
 * Formats used space as a percentage. Values close to the edges are not rounded to 0% or 100%, so a
 * nearly empty or nearly full volume isn't presented as completely empty or full.
 */
internal fun formatUsedPercent(used: Long, total: Long): String {
    val percent = used.toDouble() * 100 / total
    return when {
        used <= 0L -> "0%"
        used >= total -> "100%"
        percent < 1 -> "<1%"
        percent > 99 -> ">99%"
        else -> "${percent.roundToInt()}%"
    }
}

@Preview
@Composable
fun StorageUsageBarPreview() {
    CpuInfoTheme {
        Column(verticalArrangement = Arrangement.spacedBy(spacingSmall)) {
            StorageUsageBar(
                storageItem =
                    StorageItem(
                        id = "0",
                        label = TextResource.Text("Internal"),
                        iconDrawable = Res.drawable.baseline_folder_special_24,
                        storageTotal = 128_000_000_000,
                        storageUsed = 96_000_000_000,
                    ),
                progressHeight = 32.dp,
            )
            StorageUsageBar(
                storageItem =
                    StorageItem(
                        id = "1",
                        label = TextResource.Text("SD card"),
                        iconDrawable = Res.drawable.baseline_sd_storage_24,
                        storageTotal = -1,
                        storageUsed = -1,
                    ),
                progressHeight = 32.dp,
            )
        }
    }
}
