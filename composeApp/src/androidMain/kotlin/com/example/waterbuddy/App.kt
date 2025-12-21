package com.example.waterbuddy

import android.app.Application
import com.example.waterbuddy.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory

class App : Application() {
    val appGraph by lazy { createGraphFactory<AndroidAppGraph.Factory>().create(this) }
}
