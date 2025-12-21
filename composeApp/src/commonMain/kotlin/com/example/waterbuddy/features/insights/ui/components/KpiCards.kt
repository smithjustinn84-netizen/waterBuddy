package com.example.waterbuddy.features.insights.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.core.util.formatDate
import com.example.waterbuddy.features.insights.domain.model.HydrationInsights
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KpiGrid(insights: HydrationInsights) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            KpiCard(
                title = "Avg Intake",
                value = "${insights.averageIntake} ml",
                modifier = Modifier.weight(1f),
            )
            KpiCard(
                title = "Completion",
                value = "${insights.completionPercentage}%",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            KpiCard(
                title = "Longest Streak",
                value = "${insights.longestStreak} days",
                modifier = Modifier.weight(1f),
            )
            KpiCard(
                title = "Peak Day",
                value = insights.peakDay?.let { formatDate(it) } ?: "-",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private val sampleInsights =
    HydrationInsights(
        averageIntake = 1950,
        completionRate = 0.82f,
        longestStreak = 5,
        peakDay = LocalDate(2024, 1, 15),
        peakDayIntake = 2800,
        weeklyTrend = emptyList(),
        monthlyTrend = emptyList(),
    )

@Preview
@Composable
private fun KpiGridPreview() {
    WaterBuddyTheme {
        Surface {
            Box(Modifier.padding(16.dp)) {
                KpiGrid(insights = sampleInsights)
            }
        }
    }
}

@Preview
@Composable
private fun KpiGridDarkModePreview() {
    WaterBuddyTheme(darkTheme = true) {
        Surface {
            Box(Modifier.padding(16.dp)) {
                KpiGrid(insights = sampleInsights)
            }
        }
    }
}
