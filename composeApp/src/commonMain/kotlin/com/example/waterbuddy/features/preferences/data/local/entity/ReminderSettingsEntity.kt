package com.example.waterbuddy.features.preferences.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.waterbuddy.features.preferences.domain.model.ReminderSound
import kotlinx.datetime.LocalTime

@Entity(tableName = "reminder_settings")
data class ReminderSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean,
    val frequencyMinutes: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val sound: ReminderSound,
)
