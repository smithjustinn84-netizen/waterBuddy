package com.example.waterbuddy.features.insights.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.features.watertracker.domain.model.DailyWaterStats
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WeeklyBarChart(
    data: List<DailyWaterStats>,
    modifier: Modifier = Modifier,
    color: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Weekly Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available", style = MaterialTheme.typography.bodyMedium)
                }
                return@Column
            }

            val maxVal = data.maxOfOrNull { it.totalMl }?.coerceAtLeast(2500) ?: 2500
            val goal = data.firstOrNull()?.goalMl ?: 2000

            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw Goal Line
                    val goalY = size.height - (goal.toFloat() / maxVal.toFloat() * size.height)
                    drawLine(
                        color = color,
                        start = Offset(0f, goalY),
                        end = Offset(size.width, goalY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f),
                        cap = StrokeCap.Round,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    data.forEach { stat ->
                        val barHeightFraction =
                            (stat.totalMl.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f),
                        ) {
                            // Bar
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(0.6f)
                                        .fillMaxHeight(barHeightFraction)
                                        .background(
                                            color =
                                                if (stat.isGoalReached) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.secondary
                                                },
                                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                                        ),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                data.forEach { stat ->
                    Text(
                        text =
                            stat.date.dayOfWeek.name
                                .take(1),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private val sampleWeeklyData =
    listOf(
        DailyWaterStats(LocalDate(2024, 1, 1), 1500, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 2), 2200, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 3), 1800, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 4), 2500, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 5), 2000, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 6), 1200, 2000, emptyList()),
        DailyWaterStats(LocalDate(2024, 1, 7), 2100, 2000, emptyList()),
    )

@Preview
@Composable
private fun WeeklyBarChartPreview() {
    WaterBuddyTheme {
        Surface {
            WeeklyBarChart(
                data = sampleWeeklyData,
                modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
private fun WeeklyBarChartDarkModePreview() {
    WaterBuddyTheme(darkTheme = true) {
        Surface {
            WeeklyBarChart(
                data = sampleWeeklyData,
                modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
