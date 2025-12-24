package com.example.waterbuddy.features.preferences.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.waterbuddy.features.preferences.data.local.entity.ReminderSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderSettingsDao {
    @Query("SELECT * FROM reminder_settings WHERE id = 1")
    fun getReminderSettings(): Flow<ReminderSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderSettings(settings: ReminderSettingsEntity)
}
