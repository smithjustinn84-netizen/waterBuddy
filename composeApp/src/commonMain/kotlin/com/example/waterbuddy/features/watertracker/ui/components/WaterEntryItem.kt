package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import com.example.waterbuddy.core.util.formatTime
import com.example.waterbuddy.features.watertracker.domain.model.WaterIntake
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun WaterEntryItem(
    entry: WaterIntake,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "💧",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                Column {
                    Text(
                        text = "${entry.amountMl}ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = formatTime(entry.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            TextButton(onClick = onDelete) {
                Text(
                    text = "🗑️",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun WaterEntryItemPreview() {
    WaterBuddyTheme {
        Surface {
            WaterEntryItem(
                entry =
                    WaterIntake(
                        id = "preview",
                        amountMl = 250,
                        timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    ),
                onDelete = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun WaterEntryItemDarkModePreview() {
    WaterBuddyTheme(darkTheme = true) {
        Surface {
            WaterEntryItem(
                entry =
                    WaterIntake(
                        id = "preview",
                        amountMl = 250,
                        timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    ),
                onDelete = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
