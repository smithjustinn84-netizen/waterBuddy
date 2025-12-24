package com.example.waterbuddy.features.preferences.domain.model

import kotlinx.datetime.LocalTime

data class ReminderSettings(
    val isEnabled: Boolean = false,
    val frequencyMinutes: Int = 60,
    val startTime: LocalTime = LocalTime(9, 0),
    val endTime: LocalTime = LocalTime(21, 0),
    val sound: ReminderSound = ReminderSound.DEFAULT,
)

enum class ReminderSound {
    DEFAULT,
    MARTIAN_DRIP,
    LIFE_STREAM,
    ZENITH_BELL,
}
