package com.example.waterbuddy.features.watertracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import org.jetbrains.compose.resources.stringResource
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.add_button
import waterbuddy.composeapp.generated.resources.amount_input_label
import waterbuddy.composeapp.generated.resources.cancel_button
import waterbuddy.composeapp.generated.resources.custom_add_description
import waterbuddy.composeapp.generated.resources.custom_add_title
import waterbuddy.composeapp.generated.resources.ml_suffix

@Composable
fun CustomAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toIntOrNull() ?: 0
    val isError = amount > 4000

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.custom_add_title)) },
        text = {
            Column {
                Text(stringResource(Res.string.custom_add_description))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(Res.string.amount_input_label)) },
                    singleLine = true,
                    suffix = { Text(stringResource(Res.string.ml_suffix)) },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(
                                text = "Max amount is 4000ml",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (amount in 1..4000) {
                        onConfirm(amount)
                    }
                },
                enabled = amount in 1..4000,
            ) {
                Text(stringResource(Res.string.add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button))
            }
        },
    )
}
