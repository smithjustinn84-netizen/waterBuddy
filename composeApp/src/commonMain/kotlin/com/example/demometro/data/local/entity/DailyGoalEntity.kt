package com.example.demometro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goal")
data class DailyGoalEntity(
    @PrimaryKey
    val id: Int = 1,
    val goalMl: Int
)

