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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import kotlinx.datetime.DayOfWeek

@Composable
fun MonthlyHeatmap(
    data: List<DailyWaterStats>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Recent Streaks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val firstDayOfWeek = displayData.first().date.dayOfWeek
                repeat(7) { i ->
                    val dayOfWeek = DayOfWeek.entries[(firstDayOfWeek.ordinal + i) % 7]
                    Text(
                        text = dayOfWeek.name.take(1),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
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
                                if (stat.isGoalReached) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(
                                        color = color,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stat.date.dayOfMonth}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (stat.progressPercentage > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
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
        }
    }
}
