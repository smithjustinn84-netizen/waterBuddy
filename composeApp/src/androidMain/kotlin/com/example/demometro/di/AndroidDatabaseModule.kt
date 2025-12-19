package com.example.demometro.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.demometro.data.local.AppDatabase
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AndroidDatabaseModule {
    @Provides
    fun provideDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "app.db"
        )
    }
}

