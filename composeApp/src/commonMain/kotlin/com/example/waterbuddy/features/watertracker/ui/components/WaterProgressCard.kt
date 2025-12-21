package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.consumed_label
import waterbuddy.composeapp.generated.resources.exceeded_label
import waterbuddy.composeapp.generated.resources.goal_label
import waterbuddy.composeapp.generated.resources.remaining_label

@Composable
fun WaterProgressCard(
    totalMl: Int,
    goalMl: Int,
    progressPercentage: Float,
    remainingMl: Int,
    isGoalReached: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(durationMillis = 1000)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGoalReached)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Water Drop Icon with progress
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "💧",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = stringResource(Res.string.consumed_label), value = "${totalMl}ml")
                StatItem(label = stringResource(Res.string.goal_label), value = "${goalMl}ml")
                StatItem(
                    label = if (isGoalReached) stringResource(Res.string.exceeded_label) else stringResource(
                        Res.string.remaining_label
                    ),
                    value = if (isGoalReached) "+${totalMl - goalMl}ml" else "${remainingMl}ml"
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun WaterProgressCardPreview() {
    MaterialTheme {
        WaterProgressCard(
            totalMl = 1200,
            goalMl = 2000,
            progressPercentage = 0.6f,
            remainingMl = 800,
            isGoalReached = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
fun WaterProgressCardGoalReachedPreview() {
    MaterialTheme {
        WaterProgressCard(
            totalMl = 2500,
            goalMl = 2000,
            progressPercentage = 1.0f,
            remainingMl = 0,
            isGoalReached = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}
