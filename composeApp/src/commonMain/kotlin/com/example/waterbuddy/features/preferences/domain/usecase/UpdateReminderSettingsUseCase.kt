package com.example.waterbuddy.features.preferences.domain.usecase

import com.example.waterbuddy.features.preferences.domain.model.ReminderSettings
import com.example.waterbuddy.features.preferences.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject

@Inject
class UpdateReminderSettingsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(settings: ReminderSettings): Result<Unit> =
        settingsRepository.updateReminderSettings(settings)
}
