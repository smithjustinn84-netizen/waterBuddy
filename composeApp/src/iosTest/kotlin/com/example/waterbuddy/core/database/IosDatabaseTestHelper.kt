package com.example.waterbuddy.core.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> = Room.inMemoryDatabaseBuilder<AppDatabase>()
