package com.example.waterbuddy.features.watertracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey
    val id: String,
    val amountMl: Int,
    val timestamp: LocalDateTime,
    val note: String? = null,
)
