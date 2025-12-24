package com.example.waterbuddy.features.preferences.domain.usecase

import com.example.waterbuddy.features.preferences.domain.model.ReminderSettings
import com.example.waterbuddy.features.preferences.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class GetReminderSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<ReminderSettings> = settingsRepository.getReminderSettings()
}
