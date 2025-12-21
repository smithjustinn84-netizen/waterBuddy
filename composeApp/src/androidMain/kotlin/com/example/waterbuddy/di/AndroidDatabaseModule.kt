package com.example.waterbuddy.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.waterbuddy.core.database.AppDatabase
import com.example.waterbuddy.core.di.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AndroidDatabaseModule {
    @Provides
    fun provideDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> =
        Room
            .databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "app.db",
            ).setDriver(BundledSQLiteDriver())
}
