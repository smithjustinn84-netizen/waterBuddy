package com.example.demometro.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.demometro.data.local.AppDatabase
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import platform.Foundation.NSHomeDirectory

@ContributesTo(AppScope::class)
interface IosDatabaseModule {
    @Provides
    fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/app.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath
        )
    }
}

