package com.kgurgul.cpuinfo.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall

@Composable
fun ItemValueRow(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
            modifier = Modifier.weight(.4f),
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

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ItemValueRowPreview() {
    CpuInfoTheme {
        ItemValueRow(
            title = "Title",
            value = "Value",
        )
    }
}