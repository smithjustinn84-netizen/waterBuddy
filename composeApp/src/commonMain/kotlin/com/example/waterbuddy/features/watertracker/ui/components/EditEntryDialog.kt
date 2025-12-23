package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.features.watertracker.domain.model.WaterIntake
import org.jetbrains.compose.resources.stringResource
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.amount_input_label
import waterbuddy.composeapp.generated.resources.cancel_button
import waterbuddy.composeapp.generated.resources.edit_entry_description
import waterbuddy.composeapp.generated.resources.edit_entry_title
import waterbuddy.composeapp.generated.resources.ml_suffix
import waterbuddy.composeapp.generated.resources.save_button

@Composable
fun EditEntryDialog(
    entry: WaterIntake,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var amountText by remember { mutableStateOf(entry.amountMl.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.edit_entry_title)) },
        text = {
            Column {
                Text(stringResource(Res.string.edit_entry_description))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(Res.string.amount_input_label)) },
                    singleLine = true,
                    suffix = { Text(stringResource(Res.string.ml_suffix)) },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toIntOrNull() ?: entry.amountMl
                    onConfirm(amount)
                },
            ) {
                Text(stringResource(Res.string.save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        },
    )
}
