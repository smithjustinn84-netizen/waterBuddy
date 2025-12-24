package com.example.waterbuddy.features.insights.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.arid_sols_label
import waterbuddy.composeapp.generated.resources.grokked_label
import waterbuddy.composeapp.generated.resources.recent_streaks_title

@Composable
fun MonthlyHeatmap(
    data: List<DailyWaterStats>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(Res.string.recent_streaks_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available", style = MaterialTheme.typography.bodyMedium)
                }
                return@Column
            }

            val displayData = data.takeLast(28)

            // Day Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val firstDayOfWeek = displayData.first().date.dayOfWeek
                repeat(7) { i ->
                    val dayOfWeek = DayOfWeek.entries[(firstDayOfWeek.ordinal + i) % 7]
                    Text(
                        text = dayOfWeek.name.take(1),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Heatmap Grid (Calendar style)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                displayData.chunked(7).forEach { weekData ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        weekData.forEach { stat ->
                            val alpha = (stat.progressPercentage).coerceIn(0.1f, 1f)
                            val color =
                                if (stat.isGoalReached) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                                }
                            Box(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(
                                            color = color,
                                            shape = RoundedCornerShape(6.dp),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${stat.date.day}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (stat.progressPercentage > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                        // Add spacers for the rest of the week if it's not a full week
                        val emptyCells = 7 - weekData.size
                        if (emptyCells > 0) {
                            repeat(emptyCells) {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Heatmap Key
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.arid_sols_label),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                repeat(5) { i ->
                    val alpha = 0.2f + (i * 0.2f)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                                shape = RoundedCornerShape(2.dp),
                            ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = stringResource(Res.string.grokked_label),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private val sampleMonthlyData =
    (1..28).map { i ->
        DailyWaterStats(
            date = LocalDate(2024, 1, i),
            totalMl = (1000..2500).random(),
            goalMl = 2000,
            entries = emptyList(),
        )
    }

@Preview
@Composable
private fun MonthlyHeatmapPreview() {
    WaterBuddyTheme {
        Surface {
            MonthlyHeatmap(
                data = sampleMonthlyData,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }
}
