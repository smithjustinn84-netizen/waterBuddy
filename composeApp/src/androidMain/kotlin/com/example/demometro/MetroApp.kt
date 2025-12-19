package com.example.demometro

import android.app.Application
import com.example.demometro.di.AndroidAppComponent
import dev.zacsweers.metro.createGraphFactory

class MetroApp : Application() {
    val appGraph by lazy { createGraphFactory<AndroidAppComponent.Factory>().create(this) }
}