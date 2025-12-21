package com.example.waterbuddy.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
    Room.inMemoryDatabaseBuilder(
        context = ApplicationProvider.getApplicationContext(),
        klass = AppDatabase::class.java,
    )

actual fun createTestDatabase(): AppDatabase =
    getDatabaseBuilder().build()
