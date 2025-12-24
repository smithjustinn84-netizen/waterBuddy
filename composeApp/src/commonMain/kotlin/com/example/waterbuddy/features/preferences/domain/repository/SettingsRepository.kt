package com.example.waterbuddy.features.preferences.domain.repository

import com.example.waterbuddy.features.preferences.domain.model.ReminderSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getReminderSettings(): Flow<ReminderSettings>
    suspend fun updateReminderSettings(settings: ReminderSettings): Result<Unit>
}
