package com.example.waterbuddy.features.watertracker.domain.model

import kotlinx.datetime.LocalDateTime

data class WaterIntake(
    val id: String,
    val amountMl: Int,
    val timestamp: LocalDateTime,
    val note: String? = null
)