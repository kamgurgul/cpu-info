package com.kgurgul.cpuinfo.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemValueRow(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    SelectionContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier),
        ) {
            val titleWeight = if (value == null) 1f else .4f
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                modifier = Modifier.weight(titleWeight),
            )
            value?.let {
                Spacer(modifier = Modifier.size(spacingXSmall))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = .7f),
                    modifier = Modifier.weight(.6f),
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemValueRowPreview() {
    CpuInfoTheme {
        ItemValueRow(
            title = "Title",
            value = "Value",
        )
    }
}
