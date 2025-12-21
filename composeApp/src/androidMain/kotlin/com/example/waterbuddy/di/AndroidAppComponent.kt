package com.example.waterbuddy.di

import android.app.Application
import android.content.Context
import com.example.waterbuddy.core.di.AppComponent
import com.example.waterbuddy.core.di.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
abstract class AndroidAppComponent : AppComponent {

    @Provides fun provideApplicationContext(application: Application): Context = application
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AndroidAppComponent
    }
}
