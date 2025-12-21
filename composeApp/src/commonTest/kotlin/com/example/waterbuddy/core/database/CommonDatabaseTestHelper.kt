package com.example.waterbuddy.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

fun createTestDatabase(): AppDatabase =
    getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .build()
