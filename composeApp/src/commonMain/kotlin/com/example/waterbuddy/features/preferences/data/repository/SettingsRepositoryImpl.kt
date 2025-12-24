package com.example.waterbuddy.features.preferences.data.repository

import com.example.waterbuddy.core.di.AppScope
import com.example.waterbuddy.features.preferences.data.local.dao.ReminderSettingsDao
import com.example.waterbuddy.features.preferences.data.local.entity.ReminderSettingsEntity
import com.example.waterbuddy.features.preferences.domain.model.ReminderSettings
import com.example.waterbuddy.features.preferences.domain.repository.SettingsRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class SettingsRepositoryImpl(
    private val reminderSettingsDao: ReminderSettingsDao,
) : SettingsRepository {
    override fun getReminderSettings(): Flow<ReminderSettings> =
        reminderSettingsDao.getReminderSettings().map { entity ->
            entity?.toDomain() ?: ReminderSettings()
        }

    override suspend fun updateReminderSettings(settings: ReminderSettings): Result<Unit> =
        try {
            reminderSettingsDao.insertReminderSettings(settings.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun ReminderSettingsEntity.toDomain(): ReminderSettings =
        ReminderSettings(
            isEnabled = isEnabled,
            frequencyMinutes = frequencyMinutes,
            startTime = startTime,
            endTime = endTime,
            sound = sound,
        )

    private fun ReminderSettings.toEntity(): ReminderSettingsEntity =
        ReminderSettingsEntity(
            isEnabled = isEnabled,
            frequencyMinutes = frequencyMinutes,
            startTime = startTime,
            endTime = endTime,
            sound = sound,
        )
}
