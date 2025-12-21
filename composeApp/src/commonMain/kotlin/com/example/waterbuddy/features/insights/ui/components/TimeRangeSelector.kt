package com.example.waterbuddy.features.insights.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.features.insights.ui.TimeRange
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TimeRange.entries.forEach { range ->
                val isSelected = range == selectedRange
                val containerColor =
                    if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                val contentColor =
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                val shadowElevation = if (isSelected) 2.dp else 0.dp

                Surface(
                    onClick = { onRangeSelected(range) },
                    shape = RoundedCornerShape(50),
                    color = containerColor,
                    contentColor = contentColor,
                    shadowElevation = shadowElevation,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = range.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimeRangeSelectorWeekPreview() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            TimeRangeSelector(
                selectedRange = TimeRange.WEEK,
                onRangeSelected = {}
            )
        }
    }
}

@Preview
@Composable
private fun TimeRangeSelectorMonthPreview() {
    MaterialTheme {
        Box(Modifier.padding(16.dp)) {
            TimeRangeSelector(
                selectedRange = TimeRange.MONTH,
                onRangeSelected = {}
            )
        }
    }
}
