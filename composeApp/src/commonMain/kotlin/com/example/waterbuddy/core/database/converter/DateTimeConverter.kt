package com.example.waterbuddy.core.database.converter

import androidx.room.TypeConverter
import com.example.waterbuddy.features.preferences.domain.model.ReminderSound
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class DateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? = date?.toString()

    @TypeConverter
    fun fromLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun localTimeToTimestamp(time: LocalTime?): String? = time?.toString()

    @TypeConverter
    fun fromReminderSound(value: String?): ReminderSound? = value?.let { ReminderSound.valueOf(it) }

    @TypeConverter
    fun reminderSoundToString(sound: ReminderSound?): String? = sound?.name
}
