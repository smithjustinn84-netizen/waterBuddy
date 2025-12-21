package com.example.waterbuddy

import android.app.Application
import com.example.waterbuddy.di.AndroidAppComponent
import dev.zacsweers.metro.createGraphFactory

class App : Application() {
    val appGraph by lazy { createGraphFactory<AndroidAppComponent.Factory>().create(this) }
}