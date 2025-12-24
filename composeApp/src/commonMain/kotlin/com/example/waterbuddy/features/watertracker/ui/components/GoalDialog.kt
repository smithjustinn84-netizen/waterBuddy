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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.waterbuddy.features.watertracker.domain.usecase.UpdateDailyGoalUseCase
import org.jetbrains.compose.resources.stringResource
import waterbuddy.composeapp.generated.resources.Res
import waterbuddy.composeapp.generated.resources.cancel_button
import waterbuddy.composeapp.generated.resources.goal_input_label
import waterbuddy.composeapp.generated.resources.goal_range_label
import waterbuddy.composeapp.generated.resources.ml_suffix
import waterbuddy.composeapp.generated.resources.save_button
import waterbuddy.composeapp.generated.resources.set_goal_description
import waterbuddy.composeapp.generated.resources.set_goal_title

@Composable
fun GoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var goalText by remember { mutableStateOf((currentGoal).toString()) }
    val goalValue by remember { derivedStateOf { goalText.toIntOrNull() ?: 0 } }
    val isError by remember {
        derivedStateOf {
            goalValue < UpdateDailyGoalUseCase.MIN_GOAL || goalValue > UpdateDailyGoalUseCase.MAX_GOAL
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.set_goal_title)) },
        text = {
            Column {
                Text(stringResource(Res.string.set_goal_description))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(Res.string.goal_input_label)) },
                    singleLine = true,
                    suffix = { Text(stringResource(Res.string.ml_suffix)) },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(
                                text = stringResource(
                                    Res.string.goal_range_label,
                                    UpdateDailyGoalUseCase.MIN_GOAL,
                                    UpdateDailyGoalUseCase.MAX_GOAL,
                                    stringResource(Res.string.ml_suffix),
                                ),
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
                    if (!isError) {
                        onConfirm(goalValue)
                    }
                },
                enabled = !isError,
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
