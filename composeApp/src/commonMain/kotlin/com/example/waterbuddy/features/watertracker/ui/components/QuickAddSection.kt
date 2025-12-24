package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.core.theme.WaterBuddyTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.bottle_label
import waterbuddy.composeapp.generated.resources.custom_label
import waterbuddy.composeapp.generated.resources.glass_label
import waterbuddy.composeapp.generated.resources.large_label
import waterbuddy.composeapp.generated.resources.quick_add_title

@Composable
fun QuickAddSection(
    onAddWater: (Int) -> Unit,
    onShowCustomAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.quick_add_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickAddButton(
                    amount = 250,
                    label = stringResource(Res.string.glass_label),
                    emoji = "🏺",
                    onClick = { onAddWater(250) },
                    modifier = Modifier.weight(1f),
                )
                QuickAddButton(
                    amount = 500,
                    label = stringResource(Res.string.bottle_label),
                    emoji = "🍶",
                    onClick = { onAddWater(500) },
                    modifier = Modifier.weight(1f),
                )
                QuickAddButton(
                    amount = 750,
                    label = stringResource(Res.string.large_label),
                    emoji = "🌊",
                    onClick = { onAddWater(750) },
                    modifier = Modifier.weight(1f),
                )
                QuickAddButton(
                    label = stringResource(Res.string.custom_label),
                    emoji = "👽",
                    onClick = onShowCustomAdd,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun QuickAddButton(
    amount: Int? = null,
    label: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (amount != null) {
                Text(text = "${amount}ml", style = MaterialTheme.typography.bodySmall)
            } else {
                Text(text = "???ml", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview
@Composable
fun QuickAddSectionPreview() {
    WaterBuddyTheme {
        Surface {
            QuickAddSection(onAddWater = {}, onShowCustomAdd = {}, modifier = Modifier.padding(16.dp))
        }
    }
}
