package com.example.waterbuddy.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return Room.inMemoryDatabaseBuilder<AppDatabase>(
        context = ApplicationProvider.getApplicationContext(),
        klass = AppDatabase::class.java
    )
}
